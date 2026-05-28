import { useState } from "react";
import { DoorOpenIcon, MoreVerticalIcon } from "lucide-react";
import type { Conversation } from "../api/types";
import { useUser } from "../UserContext";
import { useChatStore } from "../state/ChatStore";

type Props = {
    conversation: Conversation;
};

export default function ConversationHeader({ conversation }: Props) {
    const { username } = useUser();
    const { leaveConversation, typingBy } = useChatStore();
    const [menuOpen, setMenuOpen] = useState(false);
    const [leaving, setLeaving] = useState(false);

    const others = conversation.participants
        .map((p) => p.username)
        .filter((u) => u !== username);

    const title =
        others.length === 0
            ? "(only you)"
            : others.length === 1
                ? others[0]
                : others.join(", ");

    const subtitle =
        conversation.participants.length === 2
            ? `@${others[0] ?? username}`
            : `${conversation.participants.length} participants`;

    const typers = typingBy(conversation.id);

    const handleLeave = async () => {
        const ok = window.confirm("Leave this conversation?");
        if (!ok) return;
        setLeaving(true);
        try {
            await leaveConversation(conversation.id);
        } catch (e) {
            window.alert(`Failed to leave: ${e}`);
        } finally {
            setLeaving(false);
            setMenuOpen(false);
        }
    };

    return (
        <div className="flex items-center justify-between w-full bg-background-400 px-6 py-3 border-b border-background-200">
            <div className="flex gap-3 items-center min-w-0">
                <div className="rounded-full flex aspect-square w-11 min-w-11 items-center justify-center bg-accent-400">
                    <span className="text-lg text-foreground-100">
                        {(others[0] ?? username).charAt(0).toUpperCase()}
                    </span>
                </div>
                <div className="flex flex-col min-w-0">
                    <span className="text-foreground-100 text-base truncate">
                        {title}
                    </span>
                    <span className="text-muted text-xs truncate">
                        {typers.length
                            ? `${typers.join(", ")} typing…`
                            : subtitle}
                    </span>
                </div>
            </div>

            <div className="relative">
                <button
                    onClick={() => setMenuOpen((v) => !v)}
                    className="p-1 text-muted hover:text-foreground-100 transition"
                    aria-label="Conversation actions"
                >
                    <MoreVerticalIcon size={18} />
                </button>
                {menuOpen && (
                    <div className="absolute right-0 top-full mt-1 bg-background-300 border border-background-200 rounded shadow-lg py-1 z-10 min-w-[160px]">
                        <button
                            onClick={handleLeave}
                            disabled={leaving}
                            className="flex items-center gap-2 px-3 py-2 text-sm w-full text-left text-nord12 hover:bg-background-200 disabled:opacity-50"
                        >
                            <DoorOpenIcon size={14} />
                            {leaving ? "Leaving…" : "Leave conversation"}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
