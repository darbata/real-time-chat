package io.darbata.chat.message;

record SendMessageDTO (
    long conversationId,
    long to,
    String content
) {};