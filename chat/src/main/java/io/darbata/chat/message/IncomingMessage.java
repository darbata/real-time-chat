package io.darbata.chat.message;

public record IncomingMessage (
    long conversationId,
    String userId,
    String content
) {}