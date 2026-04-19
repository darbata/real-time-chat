package io.darbata.chat.message;

public record IncomingMessage (
    long conversationId,
    String senderId,
    String content
) {}