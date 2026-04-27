type ConversationHeaderProps = {
    conversationId: number;
    name: string;
    username: string;
}

export default function DirectConversationHeader({name, username} : ConversationHeaderProps) {
    return (
        <div className="flex items-center justify-between w-full bg-background-400 px-8 py-4 border-b border-accent-400">
            <div className="flex gap-2 w-full">
                <div className="rounded-full flex aspect-square w-12 min-w-12 items-center justify-center bg-accent-400">
                    <span className="text-lg text-foreground-100">
                        {name.split("")[0].toUpperCase()}
                    </span>
                </div>
                <div className="flex flex-col">
                    <span className="text-foreground-300 text-lg">{name}</span>
                    <span className="text-muted text-sm">@{username}</span>
                </div>
            </div>
        </div>
    )
}