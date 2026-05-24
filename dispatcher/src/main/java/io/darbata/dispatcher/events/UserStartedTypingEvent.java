package io.darbata.dispatcher.events;

public record UserStartedTypingEvent(
    Long conversationId
) { }