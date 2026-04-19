package io.darbata.chat.message;

import java.time.Instant;
import java.util.List;

public record DispatchMessageEvent (
        long conversationId,
        String messageId,
        String userId,
        String content,
        List<String> recipients,
        Instant sentAt
) {
    @Override
    public String toString() {
        return "DispatchMessageEvent{" +
                "conversationId=" + conversationId +
                ", messageId='" + messageId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", recipients=" + recipients +
                ", sentAt=" + sentAt +
                '}';
    }
}
