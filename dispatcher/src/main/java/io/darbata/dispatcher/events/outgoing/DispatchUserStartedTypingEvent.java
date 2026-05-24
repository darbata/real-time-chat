package io.darbata.dispatcher.events.outgoing;

import io.darbata.dispatcher.events.incoming.UserStartedTypingEvent;

import java.util.List;

public record DispatchUserStartedTypingEvent(
    UserStartedTypingEvent event,
    List<String> recipients
) { }

