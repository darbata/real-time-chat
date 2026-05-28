import { config } from "../config";
import type {
    Conversation,
    CreateConversationRequest,
    FetchedMessages,
} from "./types";


class ApiError extends Error {
    constructor(
        message: string,
        readonly status: number,
        readonly body: string,
    ) {
        super(message);
        this.name = "ApiError";
    }
}

async function request<T>(
    path: string,
    userId: string,
    init: RequestInit = {},
): Promise<T> {
    const url = `${config.conversationsApiUrl}${path}`;

    const headers = new Headers(init.headers);
    headers.set("X-User-Id", userId);
    if (init.body && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const res = await fetch(url, { ...init, headers });

    if (!res.ok) {
        const body = await res.text().catch(() => "");
        throw new ApiError(
            `${init.method ?? "GET"} ${path} -> ${res.status}`,
            res.status,
            body,
        );
    }

    // 204 No Content has no body
    if (res.status === 204) return undefined as T;

    const text = await res.text();
    return text ? (JSON.parse(text) as T) : (undefined as T);
}

export const conversationsApi = {
    list(userId: string, limit = 20, offset = 0): Promise<Conversation[]> {
        return request(
            `/conversations?limit=${limit}&offset=${offset}`,
            userId,
        );
    },

    getById(userId: string, conversationId: number): Promise<Conversation> {
        return request(`/conversations/${conversationId}`, userId);
    },

    getParticipants(
        userId: string,
        conversationId: number,
    ): Promise<string[]> {
        return request(
            `/conversations/${conversationId}/participants`,
            userId,
        );
    },

    getMessages(
        userId: string,
        conversationId: number,
        opts: { before?: string; limit?: number } = {},
    ): Promise<FetchedMessages> {
        const params = new URLSearchParams();
        if (opts.before) params.set("before", opts.before);
        if (opts.limit !== undefined) params.set("limit", String(opts.limit));
        const qs = params.toString();
        return request(
            `/conversations/${conversationId}/messages${qs ? `?${qs}` : ""}`,
            userId,
        );
    },

    create(
        userId: string,
        body: CreateConversationRequest,
    ): Promise<Conversation> {
        return request(`/conversations`, userId, {
            method: "POST",
            body: JSON.stringify(body),
        });
    },

    leave(userId: string, conversationId: number): Promise<void> {
        return request(`/conversations/${conversationId}/leave`, userId, {
            method: "DELETE",
        });
    },

    updateMessage(
        userId: string,
        conversationId: number,
        messageId: string,
        content: string,
    ): Promise<void> {
        return request(
            `/conversations/${conversationId}/messages/${messageId}`,
            userId,
            {
                method: "PUT",
                // The dispatcher's @RequestBody String reads the raw body as
                // text. Sending content-type text/plain matches that contract;
                // a JSON-encoded string would arrive with surrounding quotes.
                headers: { "Content-Type": "text/plain" },
                body: content,
            },
        );
    },

    deleteMessage(
        userId: string,
        conversationId: number,
        messageId: string,
    ): Promise<void> {
        return request(
            `/conversations/${conversationId}/messages/${messageId}`,
            userId,
            { method: "DELETE" },
        );
    },
};

export { ApiError };