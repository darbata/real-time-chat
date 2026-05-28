import type { Conversation } from "../api/types";
import ConversationItem from "./ConversationItem";

type ConversationsListProps = {
    conversations: Conversation[];
    selectedConversationId: number | null;
    setSelectedConversationId: (id: number) => void;
};

export default function ConversationsList({
    conversations,
    selectedConversationId,
    setSelectedConversationId,
}: ConversationsListProps) {
    return (
        <div className="flex flex-col gap-2">
            {conversations.map((conversation) => (
                <ConversationItem
                    key={conversation.id}
                    conversation={conversation}
                    selectedId={selectedConversationId}
                    setSelected={() =>
                        setSelectedConversationId(conversation.id)
                    }
                />
            ))}
        </div>
    );
}
