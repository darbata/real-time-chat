package io.darbata.dispatcher;

public record CreateChatRequestDTO(
        long conversationId,
        String senderId,
        String content
) { }
