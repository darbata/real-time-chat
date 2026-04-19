package io.darbata.dispatcher;

public record IncomingChat(
    long conversationId,
    String senderId,
    String content
) { }