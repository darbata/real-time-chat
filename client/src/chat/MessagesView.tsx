import { useEffect, useRef } from "react";
import { ChevronUpIcon } from "lucide-react";
import type { Chat } from "../api/types";
import MessageItem from "./MessageItem";
import { useUser } from "../UserContext";

type Props = {
    messages: Chat[];
    typers: string[];
    loading: boolean;
    hasMore: boolean;
    onLoadOlder: () => void;
    onEdit: (messageId: string, content: string) => Promise<void> | void;
    onDelete: (messageId: string) => Promise<void> | void;
};

export default function MessagesView({
    messages,
    typers,
    loading,
    hasMore,
    onLoadOlder,
    onEdit,
    onDelete,
}: Props) {
    const { username } = useUser();
    const scrollRef = useRef<HTMLDivElement>(null);
    const lastMessageId = messages.at(-1)?.id ?? null;

    // Auto-scroll to bottom when a new message arrives.
    useEffect(() => {
        const el = scrollRef.current;
        if (!el) return;
        // Only auto-scroll if user is already near the bottom — avoids
        // yanking the view while they're reading history.
        const nearBottom =
            el.scrollHeight - el.scrollTop - el.clientHeight < 120;
        if (nearBottom) el.scrollTop = el.scrollHeight;
    }, [lastMessageId, typers.length]);

    return (
        <div
            ref={scrollRef}
            className="flex flex-col px-6 py-4 gap-2 flex-1 overflow-y-auto"
        >
            {hasMore && (
                <button
                    onClick={onLoadOlder}
                    disabled={loading}
                    className="self-center text-xs text-muted hover:text-accent-200 flex items-center gap-1 px-2 py-1 rounded disabled:opacity-50"
                >
                    <ChevronUpIcon size={12} />
                    {loading ? "Loading…" : "Load older"}
                </button>
            )}

            {messages.length === 0 && !loading && (
                <p className="text-muted text-sm text-center mt-8">
                    No messages yet
                </p>
            )}

            {messages.map((message) => (
                <MessageItem
                    key={message.id}
                    message={message}
                    fromMe={message.senderId === username}
                    onEdit={onEdit}
                    onDelete={onDelete}
                />
            ))}

            {typers.length > 0 && (
                <div className="flex items-center gap-2 text-xs text-muted italic mt-1">
                    <TypingDots />
                    <span>
                        {typers.join(", ")}{" "}
                        {typers.length === 1 ? "is" : "are"} typing
                    </span>
                </div>
            )}
        </div>
    );
}

function TypingDots() {
    return (
        <span className="inline-flex gap-1 items-end">
            <span className="w-1 h-1 bg-muted rounded-full animate-bounce [animation-delay:-0.3s]" />
            <span className="w-1 h-1 bg-muted rounded-full animate-bounce [animation-delay:-0.15s]" />
            <span className="w-1 h-1 bg-muted rounded-full animate-bounce" />
        </span>
    );
}
