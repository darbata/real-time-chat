import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useRef,
    useState,
} from "react";
import type { ReactNode } from "react";
import { conversationsApi } from "../api/conversations";
import type { Chat, Conversation } from "../api/types";
import { useUser } from "../UserContext";
import { useStomp, type ConnectionStatus, type WireEvent } from "../ws/useStomp";

// Per-conversation message buffer. We keep messages sorted oldest-first
// for natural top-to-bottom render. The dispatcher returns newest-first
// with `before` as the oldest id; we reverse on insert.
type MessageBucket = {
    messages: Chat[];
    // null = haven't loaded yet, undefined = no more pages, string = next cursor
    cursor: string | null | undefined;
    loading: boolean;
};

// Active typing indicator per conversation -> userId -> expiry timestamp.
type TypingMap = Record<number, Record<string, number>>;

const TYPING_TTL_MS = 3500;

type ChatStoreValue = {
    // connection
    connectionStatus: ConnectionStatus;
    connectionError: string | null;

    // conversations list
    conversations: Conversation[];
    conversationsLoading: boolean;
    conversationsError: string | null;
    refreshConversations: () => Promise<void>;
    createConversation: (participantIds: string[]) => Promise<Conversation>;
    leaveConversation: (conversationId: number) => Promise<void>;

    // per-conversation messages
    getBucket: (conversationId: number) => MessageBucket | undefined;
    loadMessages: (conversationId: number) => Promise<void>;
    loadOlderMessages: (conversationId: number) => Promise<void>;

    // sending
    sendChat: (conversationId: number, content: string) => void;
    sendTyping: (conversationId: number) => void;
    markRead: (conversationId: number, messageId: string) => void;
    editMessage: (
        conversationId: number,
        messageId: string,
        content: string,
    ) => Promise<void>;
    deleteMessage: (
        conversationId: number,
        messageId: string,
    ) => Promise<void>;

    // typing indicators
    typingBy: (conversationId: number) => string[];

    // event log (for debug panel)
    events: WireEvent[];
    clearEvents: () => void;
};

const ChatStoreContext = createContext<ChatStoreValue | undefined>(undefined);

const MAX_LOG_ENTRIES = 200;

export function ChatStoreProvider({ children }: { children: ReactNode }) {
    const { username } = useUser();

    const [conversations, setConversations] = useState<Conversation[]>([]);
    const [conversationsLoading, setConversationsLoading] = useState(false);
    const [conversationsError, setConversationsError] = useState<string | null>(
        null,
    );

    const [buckets, setBuckets] = useState<Record<number, MessageBucket>>({});
    const [typing, setTyping] = useState<TypingMap>({});
    const [events, setEvents] = useState<WireEvent[]>([]);

    // Refs used inside STOMP callbacks so we don't have to re-subscribe.
    const usernameRef = useRef(username);
    usernameRef.current = username;

    // ---------- conversation list ----------

    const refreshConversations = useCallback(async () => {
        if (!username) return;
        setConversationsLoading(true);
        setConversationsError(null);
        try {
            const list = await conversationsApi.list(username);
            setConversations(list);
        } catch (e) {
            setConversationsError(
                e instanceof Error ? e.message : String(e),
            );
        } finally {
            setConversationsLoading(false);
        }
    }, [username]);

    useEffect(() => {
        if (username) void refreshConversations();
        else {
            setConversations([]);
            setBuckets({});
            setTyping({});
            setEvents([]);
        }
    }, [username, refreshConversations]);

    const createConversation = useCallback(
        async (participantIds: string[]) => {
            const created = await conversationsApi.create(username, {
                participantIds,
            });
            setConversations((prev) => {
                // Avoid duplicates if the server-side dedupes
                if (prev.some((c) => c.id === created.id)) return prev;
                return [created, ...prev];
            });
            return created;
        },
        [username],
    );

    const leaveConversation = useCallback(
        async (conversationId: number) => {
            await conversationsApi.leave(username, conversationId);
            setConversations((prev) =>
                prev.filter((c) => c.id !== conversationId),
            );
            setBuckets((prev) => {
                const next = { ...prev };
                delete next[conversationId];
                return next;
            });
        },
        [username],
    );

    // ---------- messages ----------

    const getBucket = useCallback(
        (conversationId: number) => buckets[conversationId],
        [buckets],
    );

    const loadMessages = useCallback(
        async (conversationId: number) => {
            if (!username) return;
            setBuckets((prev) => ({
                ...prev,
                [conversationId]: {
                    messages: prev[conversationId]?.messages ?? [],
                    cursor: prev[conversationId]?.cursor ?? null,
                    loading: true,
                },
            }));
            try {
                const page = await conversationsApi.getMessages(
                    username,
                    conversationId,
                    { limit: 50 },
                );
                // Backend returns newest-first; reverse for natural order.
                const ordered = [...page.chats].reverse();
                setBuckets((prev) => ({
                    ...prev,
                    [conversationId]: {
                        messages: ordered,
                        cursor: page.before ?? undefined,
                        loading: false,
                    },
                }));
            } catch (e) {
                setBuckets((prev) => ({
                    ...prev,
                    [conversationId]: {
                        messages: prev[conversationId]?.messages ?? [],
                        cursor: prev[conversationId]?.cursor,
                        loading: false,
                    },
                }));
                console.error("Failed to load messages", e);
            }
        },
        [username],
    );

    const loadOlderMessages = useCallback(
        async (conversationId: number) => {
            const bucket = buckets[conversationId];
            if (!username || !bucket || !bucket.cursor || bucket.loading) return;
            setBuckets((prev) => ({
                ...prev,
                [conversationId]: { ...bucket, loading: true },
            }));
            try {
                const page = await conversationsApi.getMessages(
                    username,
                    conversationId,
                    { before: bucket.cursor, limit: 50 },
                );
                const older = [...page.chats].reverse();
                setBuckets((prev) => {
                    const current = prev[conversationId];
                    if (!current) return prev;
                    return {
                        ...prev,
                        [conversationId]: {
                            messages: [...older, ...current.messages],
                            cursor: page.before ?? undefined,
                            loading: false,
                        },
                    };
                });
            } catch (e) {
                console.error("Failed to load older messages", e);
                setBuckets((prev) => {
                    const current = prev[conversationId];
                    if (!current) return prev;
                    return {
                        ...prev,
                        [conversationId]: { ...current, loading: false },
                    };
                });
            }
        },
        [buckets, username],
    );

    const editMessage = useCallback(
        async (
            conversationId: number,
            messageId: string,
            content: string,
        ) => {
            await conversationsApi.updateMessage(
                username,
                conversationId,
                messageId,
                content,
            );
            setBuckets((prev) => {
                const bucket = prev[conversationId];
                if (!bucket) return prev;
                return {
                    ...prev,
                    [conversationId]: {
                        ...bucket,
                        messages: bucket.messages.map((m) =>
                            m.id === messageId ? { ...m, content } : m,
                        ),
                    },
                };
            });
        },
        [username],
    );

    const deleteMessage = useCallback(
        async (conversationId: number, messageId: string) => {
            await conversationsApi.deleteMessage(
                username,
                conversationId,
                messageId,
            );
            setBuckets((prev) => {
                const bucket = prev[conversationId];
                if (!bucket) return prev;
                return {
                    ...prev,
                    [conversationId]: {
                        ...bucket,
                        messages: bucket.messages.filter(
                            (m) => m.id !== messageId,
                        ),
                    },
                };
            });
        },
        [username],
    );

    // ---------- STOMP wiring ----------

    const stomp = useStomp(username, {
        onDelivered: (e) => {
            const chat = e.chat;
            setBuckets((prev) => {
                const bucket = prev[chat.conversationId];
                if (!bucket) {
                    // Conversation not yet loaded — stash the single message
                    // so when the user opens it they see something. We mark
                    // cursor=null so a subsequent loadMessages refreshes.
                    return {
                        ...prev,
                        [chat.conversationId]: {
                            messages: [chat],
                            cursor: null,
                            loading: false,
                        },
                    };
                }
                // Replace if exists (e.g. status update), append otherwise.
                const idx = bucket.messages.findIndex((m) => m.id === chat.id);
                const messages =
                    idx >= 0
                        ? bucket.messages.map((m, i) => (i === idx ? chat : m))
                        : [...bucket.messages, chat];
                return {
                    ...prev,
                    [chat.conversationId]: { ...bucket, messages },
                };
            });
            // Clear typing indicator for sender — sending implies done typing
            setTyping((prev) => {
                const forConv = prev[chat.conversationId];
                if (!forConv?.[chat.senderId]) return prev;
                const next = { ...forConv };
                delete next[chat.senderId];
                return { ...prev, [chat.conversationId]: next };
            });
        },

        onTyping: (e) => {
            const { userId, conversationId } = e.event;
            if (userId === usernameRef.current) return; // ignore own typing echo
            setTyping((prev) => {
                const forConv = prev[conversationId] ?? {};
                return {
                    ...prev,
                    [conversationId]: {
                        ...forConv,
                        [userId]: Date.now() + TYPING_TTL_MS,
                    },
                };
            });
        },

        onRead: (e) => {
            const { conversationId, messageId, userId } = e.event;
            if (userId === usernameRef.current) return; // own read receipt
            setBuckets((prev) => {
                const bucket = prev[conversationId];
                if (!bucket) return prev;
                // The read event marks `messageId` and everything before it
                // (from this user) as read. We update only the explicit one;
                // the backend persists it and clients converge from history.
                return {
                    ...prev,
                    [conversationId]: {
                        ...bucket,
                        messages: bucket.messages.map((m) =>
                            m.id === messageId ? { ...m, status: "READ" } : m,
                        ),
                    },
                };
            });
        },

        // do I add here? What us conversation variable not found in
        onConversationCreated: (e) => {
            setConversations((prev) => {
                if (prev.some((c) => c.id === e.conversation.id)) return prev;
                return [e.conversation, ...prev];
            });
        },

        onEvent: (ev) => {
            setEvents((prev) => {
                const next = [ev, ...prev];
                return next.length > MAX_LOG_ENTRIES
                    ? next.slice(0, MAX_LOG_ENTRIES)
                    : next;
            });
        },
    });

    // Periodically prune expired typing indicators
    useEffect(() => {
        const interval = setInterval(() => {
            setTyping((prev) => {
                const now = Date.now();
                let changed = false;
                const next: TypingMap = {};
                for (const [convId, users] of Object.entries(prev)) {
                    const alive: Record<string, number> = {};
                    for (const [user, exp] of Object.entries(users)) {
                        if (exp > now) alive[user] = exp;
                        else changed = true;
                    }
                    if (Object.keys(alive).length > 0) {
                        next[Number(convId)] = alive;
                    } else if (Object.keys(users).length > 0) {
                        changed = true;
                    }
                }
                return changed ? next : prev;
            });
        }, 1000);
        return () => clearInterval(interval);
    }, []);

    // ---------- send wrappers ----------

    const sendChat = useCallback(
        (conversationId: number, content: string) => {
            if (!username || !content.trim()) return;
            stomp.sendChat({
                conversationId,
                senderId: username,
                content,
            });
        },
        [username, stomp],
    );

    // Debounce typing emission so we don't flood the broker.
    const lastTypingSentRef = useRef<Record<number, number>>({});
    const sendTyping = useCallback(
        (conversationId: number) => {
            if (!username) return;
            const now = Date.now();
            const last = lastTypingSentRef.current[conversationId] ?? 0;
            // Emit at most once every 2s per conversation.
            if (now - last < 2000) return;
            lastTypingSentRef.current[conversationId] = now;
            stomp.sendTyping({ userId: username, conversationId });
        },
        [username, stomp],
    );

    const markRead = useCallback(
        (conversationId: number, messageId: string) => {
            if (!username) return;
            stomp.sendRead({ userId: username, conversationId, messageId });
        },
        [username, stomp],
    );

    const typingBy = useCallback(
        (conversationId: number) => {
            const forConv = typing[conversationId];
            if (!forConv) return [];
            const now = Date.now();
            return Object.entries(forConv)
                .filter(([, exp]) => exp > now)
                .map(([user]) => user);
        },
        [typing],
    );

    const value: ChatStoreValue = useMemo(
        () => ({
            connectionStatus: stomp.status,
            connectionError: stomp.error,
            conversations,
            conversationsLoading,
            conversationsError,
            refreshConversations,
            createConversation,
            leaveConversation,
            getBucket,
            loadMessages,
            loadOlderMessages,
            sendChat,
            sendTyping,
            markRead,
            editMessage,
            deleteMessage,
            typingBy,
            events,
            clearEvents: () => setEvents([]),
        }),
        [
            stomp.status,
            stomp.error,
            conversations,
            conversationsLoading,
            conversationsError,
            refreshConversations,
            createConversation,
            leaveConversation,
            getBucket,
            loadMessages,
            loadOlderMessages,
            sendChat,
            sendTyping,
            markRead,
            editMessage,
            deleteMessage,
            typingBy,
            events,
        ],
    );

    return (
        <ChatStoreContext.Provider value={value}>
            {children}
        </ChatStoreContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useChatStore() {
    const ctx = useContext(ChatStoreContext);
    if (!ctx) {
        throw new Error(
            "useChatStore must be used inside <ChatStoreProvider>",
        );
    }
    return ctx;
}
