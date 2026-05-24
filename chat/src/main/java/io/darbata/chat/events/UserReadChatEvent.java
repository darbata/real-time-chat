package io.darbata.chat.events;

// someone else has read a delivered chat
public record UserReadChatEvent(
    String userId,
    Long conversationId,
    String messageId
) { }