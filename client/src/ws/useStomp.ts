import { Client, type IMessage } from "@stomp/stompjs";
import { useEffect, useRef, useState } from "react";
import { config } from "../config";
import type {
    CreateConversationEvent,
    DeliveredChatEvent,
    DispatchUserReadChatEvent,
    DispatchUserStartedTypingEvent,
    UserReadChatEvent,
    UserSentChatEvent,
    UserStartedTypingEvent,
} from "../api/types";

export type WireEvent = {
    id: number;
    direction: "in" | "out";
    destination: string;
    payload: unknown;
    at: number;
};

export type ConnectionStatus =
    | "idle"
    | "connecting"
    | "connected"
    | "disconnected"
    | "error";

export type StompHandlers = {
    onDelivered?: (e: DeliveredChatEvent) => void;
    onTyping?: (e: DispatchUserStartedTypingEvent) => void;
    onRead?: (e: DispatchUserReadChatEvent) => void;
    onConversationCreated?: (e: CreateConversationEvent) => void;
    onEvent?: (e: WireEvent) => void;
};

export type StompApi = {
    status: ConnectionStatus;
    error: string | null;
    sendChat: (e: UserSentChatEvent) => void;
    sendTyping: (e: UserStartedTypingEvent) => void;
    sendRead: (e: UserReadChatEvent) => void;
};

export function useStomp(
    username: string,
    handlers: StompHandlers,
): StompApi {
    const [status, setStatus] = useState<ConnectionStatus>("idle");
    const [error, setError] = useState<string | null>(null);

    const clientRef = useRef<Client | null>(null);
    const handlersRef = useRef(handlers);
    handlersRef.current = handlers;

    const eventIdRef = useRef(0);
    const logEvent = (e: Omit<WireEvent, "id" | "at">) => {
        const ev: WireEvent = {
            ...e,
            id: ++eventIdRef.current,
            at: Date.now(),
        };
        handlersRef.current.onEvent?.(ev);
    };

    useEffect(() => {
        if (!username) return;

        const client = new Client({
            brokerURL: config.chatWsUrl,
            connectHeaders: { "X-User-Id": username },
            reconnectDelay: 3000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
            debug: () => {
            },
        });

        const parse = <T,>(msg: IMessage): T => JSON.parse(msg.body) as T;

        client.onConnect = () => {
            setStatus("connected");
            setError(null);

            client.subscribe("/user/queue/chats.delivered", (msg) => {
                const e = parse<DeliveredChatEvent>(msg);
                logEvent({
                    direction: "in",
                    destination: "/user/queue/chats.delivered",
                    payload: e,
                });
                handlersRef.current.onDelivered?.(e);
            });

            client.subscribe("/user/queue/chats.typing", (msg) => {
                const e = parse<DispatchUserStartedTypingEvent>(msg);
                logEvent({
                    direction: "in",
                    destination: "/user/queue/chats.typing",
                    payload: e,
                });
                handlersRef.current.onTyping?.(e);
            });

            client.subscribe("/user/queue/chats.read", (msg) => {
                const e = parse<DispatchUserReadChatEvent>(msg);
                logEvent({
                    direction: "in",
                    destination: "/user/queue/chats.read",
                    payload: e,
                });
                handlersRef.current.onRead?.(e);
            });

            client.subscribe("/user/queue/conversation.created", (msg) => {
                const e = parse<CreateConversationEvent>(msg);
                logEvent({
                    direction: "in",
                    destination: "/user/queue/conversation.created",
                    payload: e,
                });
                handlersRef.current.onConversationCreated?.(e);
            });
        };

        client.onWebSocketClose = () => {
            setStatus((prev) => (prev === "error" ? "error" : "disconnected"));
        };

        client.onWebSocketError = (ev) => {
            setStatus("error");
            setError(
                ev instanceof Event
                    ? "WebSocket error (open the browser console for details)"
                    : String(ev),
            );
        };

        client.onStompError = (frame) => {
            setStatus("error");
            setError(frame.headers["message"] ?? "STOMP error");
        };

        setStatus("connecting");
        client.activate();
        clientRef.current = client;

        return () => {
            clientRef.current = null;
            void client.deactivate();
            setStatus("idle");
        };
    }, [username]);

    const send = (destination: string, payload: unknown) => {
        const client = clientRef.current;
        if (!client?.connected) {
            return;
        }
        client.publish({ destination, body: JSON.stringify(payload) });
        logEvent({ direction: "out", destination, payload });
    };

    return {
        status,
        error,
        sendChat: (e) => send("/app/chat", e),
        sendTyping: (e) => send("/app/typing", e),
        sendRead: (e) => send("/app/read", e),
    };
}
