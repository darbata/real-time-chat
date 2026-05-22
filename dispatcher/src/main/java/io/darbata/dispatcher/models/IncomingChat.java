package io.darbata.dispatcher.models;

public record IncomingChat(
    long conversationId,
    String senderId,
    String content
) { }