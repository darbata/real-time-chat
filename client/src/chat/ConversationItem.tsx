import type { ConversationMetadata } from "./ConversationMetadata.tsx";
import {useEffect, useState} from "react";
import {useUser} from "../UserContext.tsx";

function formatTimeSince(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    if (seconds < 60) return `${seconds}s ago`;
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    return `${Math.floor(hours / 24)}d ago`;
}

type ConversationItemProps = {
    conversation: ConversationMetadata;
    selectedId: number;
    setSelected: () => void;
}

export default function ConversationItem({ conversation, selectedId, setSelected }: ConversationItemProps) {

    const isSelected = selectedId === conversation.id;
    const [timeSince, setTimeSince] = useState('');
    const {username} = useUser();

    const participants = conversation.participants.filter(
        (participant) => participant.id !== username
    );

    useEffect(() => {
        const update = () => {
            setTimeSince(
                formatTimeSince(Date.now() - Date.parse(conversation.lastMessageAt))
            );
        };
        update();
    }, [conversation.lastMessageAt]);

    return (
        <div
            onClick={setSelected}
            className={`flex items-center  rounded w-full text-foreground-300 px-2 py-1 hover:bg-background-100 select-none transition duration-300 ${isSelected ? "bg-background-100" : "bg-background-200"}`
        }>
            <div className="bg-accent-400 min-h-10 aspect-square flex items-center justify-center text-foreground-100 rounded-full">
                {participants[0].id.split('')[0].toUpperCase()}
            </div>

            <div className="flex flex-col w-full ml-2">
                <div className="flex justify-between text-sm">
                    <span className="text-foreground-400 text-foreground-100">{participants[0].id}</span>
                    <span className="text-xs text-muted">{timeSince}</span>
                </div>
                <p className="text-xs line-clamp-1 text-foreground-300">{conversation.lastMessage}</p>
            </div>
        </div>
    );
}