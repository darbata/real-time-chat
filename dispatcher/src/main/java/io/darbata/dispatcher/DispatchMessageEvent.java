package io.darbata.dispatcher;

import java.time.Instant;
import java.util.List;

public record DispatchMessageEvent (
    long conversationId,
    String messageId,
    String senderId,
    String content,
    List<String> recipients,
    Instant sentAt
) {
    @Override
    public String toString() {
        return "DispatchMessageEvent{" +
                "conversationId=" + conversationId +
                ", messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", recipients=" + recipients +
                ", sentAt=" + sentAt +
                '}';
    }
}