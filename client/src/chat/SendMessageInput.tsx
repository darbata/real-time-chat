import { useState } from "react";
import { SendHorizontalIcon } from "lucide-react";

type Props = {
    onSend: (content: string) => void;
    onTyping: () => void;
};

export default function SendMessageInput({ onSend, onTyping }: Props) {
    const [draft, setDraft] = useState("");

    const send = () => {
        const trimmed = draft.trim();
        if (!trimmed) return;
        onSend(trimmed);
        setDraft("");
    };

    return (
        <div className="w-full py-3 px-6 bg-background-400 text-foreground-100 flex border-t border-background-200 gap-2">
            <input
                value={draft}
                onChange={(e) => {
                    setDraft(e.target.value);
                    if (e.target.value.length > 0) onTyping();
                }}
                onKeyDown={(e) => {
                    if (e.key === "Enter" && !e.shiftKey) {
                        e.preventDefault();
                        send();
                    }
                }}
                placeholder="Message…"
                className="bg-background-200 w-full rounded px-3 py-2 text-sm placeholder:text-muted focus:outline-none focus:ring-1 focus:ring-accent-400"
            />
            <button
                onClick={send}
                disabled={!draft.trim()}
                className="aspect-square rounded p-2 bg-background-200 text-foreground-100 hover:bg-accent-400 transition disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-background-200"
            >
                <SendHorizontalIcon size={18} />
            </button>
        </div>
    );
}
