import { useState } from "react";
import {
    MessageCircleDashedIcon,
    MessageCircleIcon,
    MessageCircleCheckIcon,
    PencilIcon,
    Trash2Icon,
    XIcon,
} from "lucide-react";
import type { Chat } from "../api/types";

type Props = {
    fromMe: boolean;
    message: Chat;
    onEdit: (messageId: string, content: string) => Promise<void> | void;
    onDelete: (messageId: string) => Promise<void> | void;
};

function formatTime(iso: string): string {
    try {
        return new Date(iso).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
        });
    } catch {
        return "";
    }
}

function StatusIcon({ status }: { status: Chat["status"] }) {
    if (status === "SENDING") {
        return <MessageCircleDashedIcon size={12} className="text-foreground-100/60" />;
    }
    if (status === "DELIVERED") {
        return <MessageCircleIcon size={12} className="text-foreground-100/60" />;
    }
    if (status === "READ") {
        return <MessageCircleCheckIcon size={12} className="text-accent-200" />;
    }
    return null;
}

export default function MessageItem({
    message,
    fromMe,
    onEdit,
    onDelete,
}: Props) {
    const [editing, setEditing] = useState(false);
    const [draft, setDraft] = useState(message.content);
    const [busy, setBusy] = useState(false);

    const submitEdit = async () => {
        const trimmed = draft.trim();
        if (!trimmed || trimmed === message.content) {
            setEditing(false);
            return;
        }
        setBusy(true);
        try {
            await onEdit(message.id, trimmed);
            setEditing(false);
        } catch (e) {
            window.alert(`Failed to edit: ${e}`);
        } finally {
            setBusy(false);
        }
    };

    const submitDelete = async () => {
        if (!window.confirm("Delete this message?")) return;
        setBusy(true);
        try {
            await onDelete(message.id);
        } catch (e) {
            window.alert(`Failed to delete: ${e}`);
        } finally {
            setBusy(false);
        }
    };

    return (
        <div
            className={`group flex w-full text-foreground-100 ${
                fromMe ? "justify-end" : "justify-start"
            }`}
        >
            <div
                className={`flex flex-col max-w-[70%] ${
                    fromMe ? "items-end" : "items-start"
                }`}
            >
                {!fromMe && (
                    <span className="text-[10px] text-muted px-1 mb-0.5">
                        @{message.senderId}
                    </span>
                )}

                <div
                    className={`flex items-center gap-1 ${
                        fromMe ? "flex-row-reverse" : ""
                    }`}
                >
                    <div
                        className={`py-1.5 px-3 rounded-lg ${
                            fromMe
                                ? "bg-accent-400 rounded-br-sm"
                                : "bg-background-200 rounded-bl-sm"
                        }`}
                    >
                        {editing ? (
                            <div className="flex items-center gap-1">
                                <input
                                    autoFocus
                                    value={draft}
                                    onChange={(e) => setDraft(e.target.value)}
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter") submitEdit();
                                        if (e.key === "Escape")
                                            setEditing(false);
                                    }}
                                    disabled={busy}
                                    className="bg-background-300 text-foreground-100 text-sm rounded px-1 py-0.5 outline-none ring-1 ring-accent-200"
                                />
                                <button
                                    onClick={submitEdit}
                                    disabled={busy}
                                    className="p-0.5 hover:text-nord15"
                                >
                                    <CheckIcon size={14} />
                                </button>
                                <button
                                    onClick={() => setEditing(false)}
                                    disabled={busy}
                                    className="p-0.5 hover:text-nord12"
                                >
                                    <XIcon size={14} />
                                </button>
                            </div>
                        ) : (
                            <span className="text-sm whitespace-pre-wrap break-words">
                                {message.content}
                            </span>
                        )}
                    </div>

                    {fromMe && !editing && (
                        <div className="opacity-0 group-hover:opacity-100 transition flex gap-0.5">
                            <button
                                onClick={() => {
                                    setDraft(message.content);
                                    setEditing(true);
                                }}
                                disabled={busy}
                                title="Edit"
                                className="p-1 text-muted hover:text-accent-200"
                            >
                                <PencilIcon size={12} />
                            </button>
                            <button
                                onClick={submitDelete}
                                disabled={busy}
                                title="Delete"
                                className="p-1 text-muted hover:text-nord12"
                            >
                                <Trash2Icon size={12} />
                            </button>
                        </div>
                    )}
                </div>

                <div
                    className={`flex items-center gap-1 mt-0.5 px-1 text-[10px] text-muted ${
                        fromMe ? "flex-row-reverse" : ""
                    }`}
                >
                    <span>{formatTime(message.sentAt)}</span>
                    {fromMe && <StatusIcon status={message.status} />}
                </div>
            </div>
        </div>
    );
}
