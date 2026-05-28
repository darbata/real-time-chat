import { useEffect } from "react";
import type { Conversation } from "../api/types";
import ConversationHeader from "./ConversationHeader";
import MessagesView from "./MessagesView";
import SendMessageInput from "./SendMessageInput";
import { useChatStore } from "../state/ChatStore";
import { useUser } from "../UserContext";

type Props = {
    conversation: Conversation;
};

export default function ConversationMessages({ conversation }: Props) {
    const { username } = useUser();
    const {
        getBucket,
        loadMessages,
        loadOlderMessages,
        sendChat,
        sendTyping,
        markRead,
        editMessage,
        deleteMessage,
        typingBy,
    } = useChatStore();

    // Load messages on first selection of this conversation.
    useEffect(() => {
        const bucket = getBucket(conversation.id);
        // cursor === null means "never loaded". undefined means "loaded, no more pages".
        if (!bucket || bucket.cursor === null) {
            void loadMessages(conversation.id);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [conversation.id]);

    const bucket = getBucket(conversation.id);
    const messages = bucket?.messages ?? [];
    const typers = typingBy(conversation.id).filter((u) => u !== username);

    // When a new message lands and it's not ours, send a read receipt.
    useEffect(() => {
        if (messages.length === 0) return;
        const newest = messages[messages.length - 1];
        if (newest.senderId !== username && newest.status !== "READ") {
            markRead(conversation.id, newest.id);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [messages.length, conversation.id]);

    return (
        <div className="bg-background-400 flex flex-col flex-1 h-full min-w-0">
            <ConversationHeader conversation={conversation} />
            <MessagesView
                messages={messages}
                typers={typers}
                loading={bucket?.loading ?? false}
                hasMore={typeof bucket?.cursor === "string"}
                onLoadOlder={() => loadOlderMessages(conversation.id)}
                onEdit={(id, content) =>
                    editMessage(conversation.id, id, content)
                }
                onDelete={(id) => deleteMessage(conversation.id, id)}
            />
            <SendMessageInput
                onSend={(content) => sendChat(conversation.id, content)}
                onTyping={() => sendTyping(conversation.id)}
            />
        </div>
    );
}
