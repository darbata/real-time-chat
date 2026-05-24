package io.darbata.dispatcher.events;

import java.time.Instant;
import java.util.List;

public record DispatchChatEvent(
    long conversationId,
    String chatId,
    String senderId,
    String content,
    List<String> recipients,
    Instant sentAt
) { }