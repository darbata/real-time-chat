package io.darbata.dispatcher.events.incoming;

public record UserSentChatEvent (
    long conversationId,
    String senderId,
    String content
) { }