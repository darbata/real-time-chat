package io.darbata.chat.events;

import java.util.List;

public record DispatchUserStartedTypingEvent(
        UserStartedTypingEvent event,
        List<String> recipients
) { }

