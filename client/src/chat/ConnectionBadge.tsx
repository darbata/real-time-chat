import { useChatStore } from "../state/ChatStore";
import type { ConnectionStatus } from "../ws/useStomp";

const PRESENTATION: Record<
    ConnectionStatus,
    { label: string; dot: string; text: string; title?: string }
> = {
    idle: {
        label: "idle",
        dot: "bg-muted",
        text: "text-muted",
    },
    connecting: {
        label: "connecting",
        dot: "bg-nord14 animate-pulse",
        text: "text-nord14",
    },
    connected: {
        label: "connected",
        dot: "bg-nord15",
        text: "text-nord15",
    },
    disconnected: {
        label: "disconnected",
        dot: "bg-nord13",
        text: "text-nord13",
    },
    error: {
        label: "error",
        dot: "bg-nord12",
        text: "text-nord12",
    },
};

export default function ConnectionBadge() {
    const { connectionStatus, connectionError } = useChatStore();
    const p = PRESENTATION[connectionStatus];

    return (
        <div
            className="flex items-center gap-1"
            title={connectionError ?? undefined}
        >
            <span className={`w-2 h-2 rounded-full ${p.dot}`} />
            <span className={`text-xs ${p.text} `}>{p.label}</span>
        </div>
    );
}
