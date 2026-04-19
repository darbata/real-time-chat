package io.darbata.dispatcher;

import java.time.Instant;
import java.util.List;

public record DispatchChatEvent(
    long conversationId,
    String chatId,
    String senderId,
    String content,
    List<String> recipients,
    Instant sentAt
) {
    @Override
    public String toString() {
        return "DispatchMessageEvent{" +
                "conversationId=" + conversationId +
                ", id='" + chatId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", recipients=" + recipients +
                ", sentAt=" + sentAt +
                '}';
    }
}