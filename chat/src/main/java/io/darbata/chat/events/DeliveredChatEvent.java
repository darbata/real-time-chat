package io.darbata.chat.events;

import io.darbata.chat.models.Chat;

import java.util.List;

public record DeliveredChatEvent (
        Chat chat,
        List<String> recipients
) { }
