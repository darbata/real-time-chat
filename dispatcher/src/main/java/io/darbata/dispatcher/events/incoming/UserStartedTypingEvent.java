package io.darbata.dispatcher.events.incoming;

public record UserStartedTypingEvent(
    String userId,
    Long conversationId
) { }