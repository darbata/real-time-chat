import { MessageCircleIcon } from "lucide-react";

export default function EmptyConversationPane() {
    return (
        <div className="flex-1 flex items-center justify-center bg-background-400 text-muted">
            <div className="flex flex-col items-center gap-3">
                <MessageCircleIcon size={48} className="opacity-40" />
                <p className="text-sm">Pick a conversation to start testing.</p>
            </div>
        </div>
    );
}
