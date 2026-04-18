package io.darbata.dispatcher;

public record MessageDTO(
    long conversationId,
    String userId, // sub
    String content
) { }