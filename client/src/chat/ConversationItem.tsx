import type { Conversation } from "../api/types";
import { useUser } from "../UserContext";
import { useChatStore } from "../state/ChatStore";

type ConversationItemProps = {
    conversation: Conversation;
    selectedId: number | null;
    setSelected: () => void;
};

// Render the "other side" of the conversation. For a 1:1 DM that's the
// single other participant; for a group, name a couple plus a +N tail.
function describeOtherSide(usernames: string[]): string {
    if (usernames.length === 0) return "(just you)";
    if (usernames.length === 1) return usernames[0];
    if (usernames.length === 2) return `${usernames[0]}, ${usernames[1]}`;
    return `${usernames[0]}, ${usernames[1]} +${usernames.length - 2}`;
}

export default function ConversationItem({
    conversation,
    selectedId,
    setSelected,
}: ConversationItemProps) {
    const isSelected = selectedId === conversation.id;
    const { username } = useUser();
    const { getBucket, typingBy } = useChatStore();

    const others = conversation.participants
        .map((p) => p.username)
        .filter((u) => u !== username);

    const otherSide = describeOtherSide(others);

    // Use a stable color seed per conversation for the avatar so identical
    // initials don't all look the same.
    const accent = ACCENTS[conversation.id % ACCENTS.length];

    // Peek into the loaded messages for a preview, if any.
    const bucket = getBucket(conversation.id);
    const lastMessage = bucket?.messages.at(-1);

    const typers = typingBy(conversation.id);
    const previewText = typers.length
        ? `${typers.join(", ")} is typing…`
        : (lastMessage?.content ?? "—");

    return (
        <button
            onClick={setSelected}
            className={`flex items-center rounded w-full text-left text-foreground-300 px-2 py-2 hover:bg-background-100 select-none transition ${
                isSelected ? "bg-background-100" : "bg-background-200"
            }`}
        >
            <div
                className={`min-h-10 aspect-square flex items-center justify-center text-foreground-100 rounded-full ${accent}`}
            >
                {(others[0] ?? username).charAt(0).toUpperCase()}
            </div>

            <div className="flex flex-col w-full ml-2 min-w-0">
                <div className="flex justify-between text-sm gap-2">
                    <span className="truncate">{otherSide}</span>
                </div>
                <p
                    className={`text-xs line-clamp-1 ${
                        typers.length
                            ? "text-accent-200 italic"
                            : "text-foreground-300/70"
                    }`}
                >
                    {previewText}
                </p>
            </div>
        </button>
    );
}

const ACCENTS = [
    "bg-accent-400",
    "bg-accent-300",
    "bg-accent-200",
    "bg-nord16",
    "bg-nord15",
    "bg-nord14",
    "bg-nord13",
];
