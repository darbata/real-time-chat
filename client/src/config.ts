export const config = {
    conversationsApiUrl:
        import.meta.env.VITE_CONVERSATIONS_API_URL ?? "http://localhost:8081/api",
    chatWsUrl:
        import.meta.env.VITE_CHAT_WS_URL ?? "ws://localhost:8080/ws",
} as const;