import type {UserMessage} from "./UserMessage.tsx";
import MessageItem from "./MessageItem.tsx";
import {useUser} from "../UserContext.tsx";

type MessagesViewProps = {
    messages: UserMessage[];
}

export default function MessagesView({messages} : MessagesViewProps) {

    const {username} = useUser();

    return (
        <div className="flex flex-col px-8 gap-4 h-full py-4">
            {messages.map((message) => <MessageItem key={message.id} message={message} fromMe={message.senderId == username} />)}
        </div>
    )
}