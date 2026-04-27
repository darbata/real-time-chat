import type {UserMessage} from "./UserMessage.tsx";

type UserMessageProps = {
    fromMe: boolean;
    message: UserMessage;
}

export default function MessageItem({message, fromMe} : UserMessageProps) {
    return (
        <div className={`flex w-full text-foreground-100 ${fromMe ? "justify-end" : "justify-start"}`}>
            <div className="flex flex-col">
                <span className={`py-1 px-2 rounded ${fromMe ? "bg-accent-400" : "bg-background-200"}`}>
                    {message.content}
                </span>

                {
                    !fromMe &&
                    <span className="text-muted text-xs">
                        {message.senderId}
                    </span>
                }
            </div>

        </div>
    )
}