package io.darbata.chat.models;

import java.time.Instant;

public record Chat (
    String id,
    long conversationId,
    String senderId,
    String content,
    Instant sentAt,
    ChatStatus status
) { }