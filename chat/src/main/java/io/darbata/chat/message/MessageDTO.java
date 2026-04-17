package io.darbata.chat.message;

record MessageDTO (
        long conversationId,
        long userId, // the sender of this message
        String content
) { }