package io.darbata.dispatcher.events.incoming;

// someone else has read a delivered chat
public record UserReadChatEvent (
    String userId,
    Long conversationId,
    String messageId
) { }