package io.darbata.chat.events;

public record UserStartedTypingEvent(
    String userId,
    Long conversationId
) { }