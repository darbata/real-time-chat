import type {ConversationMetadata} from "./ConversationMetadata.tsx";
import ConversationsList from "./ConversationsList.tsx";


type ConversationSidebarProps = {
    conversations: ConversationMetadata[];
    selectedConversationId: number;
    setSelectedConversationId: (id: number) => void;
}

export default function ConversationsSidebar(
    {conversations, selectedConversationId, setSelectedConversationId} : ConversationSidebarProps
) {
    return (
        <div className="bg-background-300 w-full h-full sm:w-[320px] p-2 flex flex-col gap-2 text-foreground-100">
            <h2 className="text-xl font-semibold text-foreground-100">Conversations</h2>
            <input className="w-full px-2 py-1 mb-4 bg-background-200 rounded" type="text"/>
            <ConversationsList
                conversations={conversations}
                selectedConversationId={selectedConversationId}
                setSelectedConversationId={setSelectedConversationId}
            />
        </div>
    )
}