import type {UserMessage} from "./UserMessage.tsx";
import type {ConversationMetadata} from "./ConversationMetadata.tsx";
import DirectConversationHeader from "./ConversationHeader.tsx";
import MessagesView from "./MessagesView.tsx";
import SendMessageInput from "./SendMessageInput.tsx";



export default function ConversationMessages({conversation, messages} : {conversation: ConversationMetadata; messages: UserMessage[];}) {
    return (
        <div className="bg-background-400 flex flex-col w-full h-full">
            <DirectConversationHeader conversationId={conversation.id} name={conversation.participants[0].id} username={conversation.participants[0].id}  />
            <MessagesView messages={messages} />
            <SendMessageInput />
        </div>
    )
}