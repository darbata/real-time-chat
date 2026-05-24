package io.darbata.chat.events;

public record UserSentChatEvent(
    long conversationId,
    String senderId,
    String content
) { }