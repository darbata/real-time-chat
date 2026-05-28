// Types mirror the backend DTOs exactly so we can rely on shape-matching
// when deserializing JSON.

// ---------- conversations service ----------

// io.darbata.conversations.conversation.models.User
export type User = {
    username: string;
};

// io.darbata.conversations.conversation.dto.ConversationDTO
export type Conversation = {
    id: number;
    participants: User[];
};

// io.darbata.conversations.conversation.dto.CreateConversationRequestDTO
export type CreateConversationRequest = {
    participantIds: string[];
};

// ---------- chat / message domain ----------
// Backend models live in two places: the chat service (`io.darbata.chat.*`)
// publishes/consumes events via STOMP+RabbitMQ, the dispatcher persists chats
// in DynamoDB and serves history through the conversations API. The wire
// shapes are identical, so one set of types covers both.

export type ChatStatus = "SENDING" | "DELIVERED" | "READ";

export type Chat = {
    id: string;
    conversationId: number;
    senderId: string;
    content: string;
    sentAt: string; // Instant — ISO-8601 string over JSON
    status: ChatStatus;
};

// io.darbata.conversations.conversation.dto.FetchedMessagesDTO
// (matches dispatcher's MessagesDTO after the field rename)
export type FetchedMessages = {
    chats: Chat[];
    before: string | null;
};

// ---------- STOMP payloads sent from the client ----------

// Outbound -> /app/chat
export type UserSentChatEvent = {
    conversationId: number;
    senderId: string;
    content: string;
};

// Outbound -> /app/typing
export type UserStartedTypingEvent = {
    userId: string;
    conversationId: number;
};

// Outbound -> /app/read
export type UserReadChatEvent = {
    userId: string;
    conversationId: number;
    messageId: string;
};

// ---------- STOMP payloads received by the client ----------

// Inbound <- /user/queue/chats/delivered
export type DeliveredChatEvent = {
    chat: Chat;
    recipients: string[];
};

// Inbound <- /user/queue/chats/typing
export type DispatchUserStartedTypingEvent = {
    event: UserStartedTypingEvent;
    recipients: string[];
};

// Inbound <- /user/queue/chats/read
export type DispatchUserReadChatEvent = {
    event: UserReadChatEvent;
    recipients: string[];
};

export type CreateConversationEvent = {
    conversation: Conversation;
    recipients: string[];
}
