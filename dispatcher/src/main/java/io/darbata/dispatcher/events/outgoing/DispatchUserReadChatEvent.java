package io.darbata.dispatcher.events.outgoing;

import io.darbata.dispatcher.events.incoming.UserReadChatEvent;

import java.util.List;

public record DispatchUserReadChatEvent(
    UserReadChatEvent event,
    List<String> recipients
) { }

