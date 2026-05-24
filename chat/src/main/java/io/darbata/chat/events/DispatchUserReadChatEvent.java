package io.darbata.chat.events;

import java.util.List;

public record DispatchUserReadChatEvent(
        UserReadChatEvent event,
        List<String> recipients
) { }

