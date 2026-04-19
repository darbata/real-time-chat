package io.darbata.dispatcher;

public record IncomingMessage (
    long conversationId,
    String userId,
    String content
) { }