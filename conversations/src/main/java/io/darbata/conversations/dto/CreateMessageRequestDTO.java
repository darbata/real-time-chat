package io.darbata.conversations.dto;

public record CreateMessageRequestDTO (
    long conversationId,
    String content
) { }
