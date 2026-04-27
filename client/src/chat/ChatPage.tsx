import ConversationsSidebar from "./ConversationsSidebar.tsx";
import ConversationMessages from "./ConversationMessages.tsx";
import type {ConversationMetadata} from "./ConversationMetadata.tsx";
import type {UserMessage} from "./UserMessage.tsx";
import {useState} from "react";

const conversations: ConversationMetadata[] = [
    {
        "id": 1,
        "participants": [
            { "id": "alice_wonder" },
            { "id": "bob_builder" }
        ],
        "lastMessage": "See you tomorrow!",
        "lastMessageAt": "2026-04-26T08:30:00.000Z"
    },
    {
        "id": 2,
        "participants": [
            { "id": "charlie_dev" },
            { "id": "diana_prince" }
        ],
        "lastMessage": "Can you review my PR?",
        "lastMessageAt": "2026-04-25T21:15:00.000Z"
    },
    {
        "id": 3,
        "participants": [
            { "id": "alice_wonder" },
            { "id": "evan_codes" },
            { "id": "fiona_sharp" }
        ],
        "lastMessage": "Sprint planning is at 10am.",
        "lastMessageAt": "2026-04-25T18:45:00.000Z"
    },
    {
        "id": 4,
        "participants": [
            { "id": "george_rx" },
            { "id": "hannah_lee" }
        ],
        "lastMessage": "Haha yeah that was wild 😂",
        "lastMessageAt": "2026-04-24T14:00:00.000Z"
    },
    {
        "id": 5,
        "participants": [
            { "id": "ivan_storm" },
            { "id": "julia_fn" },
            { "id": "kyle_bytes" },
            { "id": "laura_q" }
        ],
        "lastMessage": "Anyone free for lunch?",
        "lastMessageAt": "2026-04-24T11:30:00.000Z"
    }
];

const messages: UserMessage[] = [
    { senderId: "alice_wonder", content: "Hey Bob, are we still on for tomorrow?" }, { senderId: "bob_builder", content: "Yeah for sure! What time works for you?" },
    { senderId: "alice_wonder", content: "How about 10am at the usual spot?" },
    { senderId: "bob_builder", content: "Perfect, I'll be there. Should I bring anything?" },
    { senderId: "alice_wonder", content: "Just yourself haha, I've got everything sorted" },
    { senderId: "bob_builder", content: "Sounds good 👍 See you tomorrow!" },
    { senderId: "alice_wonder", content: "See you tomorrow!" },
];


export default function ChatPage() {

    const [selectedConversationId, setSelectedConversationId] = useState(0);

    return (
        <main className="bg-background-400 w-full h-full flex">
            <ConversationsSidebar conversations={conversations} selectedConversationId={selectedConversationId} setSelectedConversationId={setSelectedConversationId}  />
            <ConversationMessages conversation={conversations[0]} messages={messages} />
        </main>
    )
}