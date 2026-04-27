import {SendHorizontalIcon} from "lucide-react";

export default function SendMessageInput() {
    return (
        <div className="w-full py-4 px-8 bg-300 text-foreground-100 flex border-t border-t-accent-400 gap-2">
            <input className="bg-background-200 w-full rounded px-2 py-1" type="text"/>
            <button className="aspect-square rounded overflow-hidden p-1 bg-background-200 text-background-300 hover:text-accent-400 transition duration-200">
                <SendHorizontalIcon className="" size={24} />
            </button>
        </div>
    )
}