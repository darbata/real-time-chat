package io.darbata.dispatcher.events;

public record UserReadChatEvent (
    Long conversationId,
    String messageId
) { }