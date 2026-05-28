import { useState } from "react";
import { XIcon } from "lucide-react";
import type { Conversation } from "../api/types";
import { useChatStore } from "../state/ChatStore";
import { useUser } from "../UserContext";

type Props = {
    onClose: () => void;
    onCreated: (c: Conversation) => void;
};

export default function NewConversationDialog({ onClose, onCreated }: Props) {
    const { username } = useUser();
    const { createConversation } = useChatStore();
    const [selected, setSelected] = useState<string[]>([]);
    const [custom, setCustom] = useState("");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const toggle = (u: string) => {
        setSelected((prev) =>
            prev.includes(u) ? prev.filter((x) => x !== u) : [...prev, u],
        );
    };

    const addCustom = () => {
        const trimmed = custom.trim();
        if (!trimmed) return;
        if (!selected.includes(trimmed)) {
            setSelected((prev) => [...prev, trimmed]);
        }
        setCustom("");
    };

    const submit = async () => {
        if (selected.length === 0) {
            setError("Pick at least one other participant");
            return;
        }
        setBusy(true);
        setError(null);
        try {
            // Service auto-adds the caller to participants, but explicit is fine
            const created = await createConversation(selected);
            onCreated(created);
        } catch (e) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setBusy(false);
        }
    };

    return (
        <div
            className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
            onClick={onClose}
        >
            <div
                onClick={(e) => e.stopPropagation()}
                className="bg-background-300 border border-background-200 rounded-lg p-6 w-full max-w-md text-foreground-100"
            >
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold">New conversation</h3>
                    <button
                        onClick={onClose}
                        className="text-muted hover:text-foreground-100"
                    >
                        <XIcon size={18} />
                    </button>
                </div>

                <p className="text-xs text-muted mb-3">
                    Search for a user then click 'Add'. You may add more than one user to create a group chat.
                </p>

                <div className="flex gap-2 mb-4">
                    <input
                        value={custom}
                        onChange={(e) => setCustom(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && addCustom()}
                        placeholder="@username"
                        className="flex-1 bg-background-200 rounded px-2 py-1 text-sm focus:outline-none focus:ring-1 focus:ring-accent-400"
                    />
                    <button
                        onClick={addCustom}
                        className="text-xs px-3 rounded bg-background-200 hover:bg-background-100"
                    >
                        Add
                    </button>
                </div>

                {selected.length > 0 && (
                    <div className="mb-3 flex flex-wrap gap-1">
                        {selected.map((u) => (
                            <span
                                key={u}
                                className="text-xs bg-accent-400/80 rounded px-2 py-0.5 flex items-center gap-1"
                            >
                                @{u}
                                <button
                                    onClick={() => toggle(u)}
                                    className="hover:text-nord12"
                                >
                                    <XIcon size={10} />
                                </button>
                            </span>
                        ))}
                    </div>
                )}

                {error && (
                    <p className="text-xs text-nord12 mb-3 break-words">
                        {error}
                    </p>
                )}

                <div className="flex justify-end gap-2">
                    <button
                        onClick={onClose}
                        className="text-sm px-3 py-1 rounded hover:bg-background-200"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={submit}
                        disabled={busy || selected.length === 0}
                        className="text-sm px-3 py-1 rounded bg-accent-400 hover:bg-accent-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {busy ? "Creating…" : "Create"}
                    </button>
                </div>
            </div>
        </div>
    );
}
