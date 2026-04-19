package io.darbata.dispatcher;

import java.time.Instant;

public record Chat (
    String id,
    long conversationId,
    String senderId,
    String content,
    Instant sentAt
) { }