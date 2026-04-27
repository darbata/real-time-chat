import type {ConversationMetadata} from "./ConversationMetadata.tsx";
import ConversationItem from "./ConversationItem.tsx";

type ConversationsListProps = {
    conversations: ConversationMetadata[];
    selectedConversationId: number;
    setSelectedConversationId: (id: number) => void;
}
export default function ConversationsList({conversations, selectedConversationId, setSelectedConversationId} : ConversationsListProps) {
    return (
        <div className="flex flex-col gap-2">
            {conversations.map((
                conversation) =>
                    <ConversationItem
                        key={conversation.id}
                        conversation={conversation}
                        selectedId={selectedConversationId}
                        setSelected={() => setSelectedConversationId(conversation.id)}
                    />
                )
            }
        </div>
    )
}