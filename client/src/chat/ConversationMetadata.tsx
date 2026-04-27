import type {User} from "./User.tsx";

export type ConversationMetadata = {
    id: number;
    participants: User[];
    lastMessage: string;
    lastMessageAt: string;
}