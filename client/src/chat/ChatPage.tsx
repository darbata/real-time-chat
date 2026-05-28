import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useUser } from "../UserContext";
import { useChatStore } from "../state/ChatStore";
import ConversationsSidebar from "./ConversationsSidebar";
import ConversationMessages from "./ConversationMessages";
import EmptyConversationPane from "./EmptyConversationPane";

export default function ChatPage() {
    const { username } = useUser();
    const navigate = useNavigate();
    const { conversations } = useChatStore();

    const [selectedConversationId, setSelectedConversationId] = useState<
        number | null
    >(null);
    const [logOpen, setLogOpen] = useState(false);

    // Bounce un-authed visitors back to /
    useEffect(() => {
        if (!username) navigate("/");
    }, [username, navigate]);

    if (!username) return null;

    const selected =
        selectedConversationId != null
            ? conversations.find((c) => c.id === selectedConversationId) ?? null
            : null;

    return (
        <main className="bg-background-400 w-full h-full flex">
            <ConversationsSidebar
                selectedConversationId={selectedConversationId}
                setSelectedConversationId={setSelectedConversationId}
                onToggleLog={() => setLogOpen((v) => !v)}
                logOpen={logOpen}
            />
            {selected ? (
                <ConversationMessages
                    key={selected.id}
                    conversation={selected}
                />
            ) : (
                <EmptyConversationPane />
            )}
        </main>
    );
}
