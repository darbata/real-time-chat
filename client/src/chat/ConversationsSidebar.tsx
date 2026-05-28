import { useState } from "react";
import { useNavigate } from "react-router";
import {
    LogOutIcon,
    PlusIcon,
    RefreshCwIcon,
    ScrollIcon,
} from "lucide-react";
import { useUser } from "../UserContext";
import { useChatStore } from "../state/ChatStore";
import ConversationsList from "./ConversationsList";
import ConnectionBadge from "./ConnectionBadge";
import NewConversationDialog from "./NewConversationDialog";

type Props = {
    selectedConversationId: number | null;
    setSelectedConversationId: (id: number) => void;
    onToggleLog: () => void;
    logOpen: boolean;
};

export default function ConversationsSidebar({
    selectedConversationId,
    setSelectedConversationId,
    onToggleLog,
    logOpen,
}: Props) {
    const { username, clearUsername } = useUser();
    const navigate = useNavigate();
    const {
        conversations,
        conversationsLoading,
        conversationsError,
        refreshConversations,
    } = useChatStore();

    const [filter, setFilter] = useState("");
    const [newOpen, setNewOpen] = useState(false);

    const filtered = filter
        ? conversations.filter((c) =>
              c.participants.some((p) =>
                  p.username.toLowerCase().includes(filter.toLowerCase()),
              ),
          )
        : conversations;

    const handleLogout = () => {
        clearUsername();
        navigate("/");
    };

    return (
        <>
            <aside className="bg-background-300 w-full h-full sm:w-[320px] p-3 flex flex-col gap-3 text-foreground-100 border-r border-background-200">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2 min-w-0">
                        <div className="w-8 h-8 rounded-full bg-accent-400 flex items-center justify-center text-sm font-semibold shrink-0">
                            {username.charAt(0).toUpperCase()}
                        </div>
                        <div className="flex flex-col min-w-0">
                            <span className="text-sm font-semibold truncate">
                                @{username}
                            </span>
                            <ConnectionBadge />
                        </div>
                    </div>
                    <button
                        onClick={handleLogout}
                        title="Log out"
                        className="text-muted hover:text-nord12 transition p-1"
                    >
                        <LogOutIcon size={18} />
                    </button>
                </div>

                <div className="flex items-center gap-2 pt-1">
                    <button
                        onClick={() => setNewOpen(true)}
                        className="flex items-center gap-1 text-xs px-2 py-1 rounded bg-accent-400 hover:bg-accent-300 transition"
                    >
                        <PlusIcon size={14} />
                        New
                    </button>
                    <button
                        onClick={() => void refreshConversations()}
                        disabled={conversationsLoading}
                        title="Refresh"
                        className="text-muted hover:text-foreground-100 transition p-1 disabled:opacity-50"
                    >
                        <RefreshCwIcon
                            size={16}
                            className={
                                conversationsLoading ? "animate-spin" : ""
                            }
                        />
                    </button>
                    <button
                        onClick={onToggleLog}
                        title="Toggle event log"
                        className={`ml-auto p-1 transition ${
                            logOpen
                                ? "text-accent-200"
                                : "text-muted hover:text-foreground-100"
                        }`}
                    >
                    </button>
                </div>

                <input
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                    placeholder="Filter by participant…"
                    className="w-full px-2 py-1 bg-background-200 rounded text-sm placeholder:text-muted focus:outline-none focus:ring-1 focus:ring-accent-400"
                />

                <div className="flex-1 overflow-y-auto -mx-1 px-1">
                    {conversationsError ? (
                        <p className="text-xs text-nord12 px-2 py-1">
                            {conversationsError}
                        </p>
                    ) : filtered.length === 0 ? (
                        <p className="text-xs text-muted px-2 py-1">
                            {conversationsLoading
                                ? "Loading…"
                                : "No conversations. Hit + to create one."}
                        </p>
                    ) : (
                        <ConversationsList
                            conversations={filtered}
                            selectedConversationId={selectedConversationId}
                            setSelectedConversationId={
                                setSelectedConversationId
                            }
                        />
                    )}
                </div>
            </aside>

            {newOpen && (
                <NewConversationDialog
                    onClose={() => setNewOpen(false)}
                    onCreated={(c) => {
                        setNewOpen(false);
                        setSelectedConversationId(c.id);
                    }}
                />
            )}
        </>
    );
}
