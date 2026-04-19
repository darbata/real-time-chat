package io.darbata.dispatcher;

public record IncomingMessage (
    long conversationId,
    String senderId,
    String content
) { }