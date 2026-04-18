package io.darbata.chat.message;

record MessageDTO (
        long conversationId,
        String userId, // the sender of this message
        String content
) { }