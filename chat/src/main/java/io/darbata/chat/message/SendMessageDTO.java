package io.darbata.chat.message;

record SendMessageDTO (
    long conversationId,
    String content
) { }