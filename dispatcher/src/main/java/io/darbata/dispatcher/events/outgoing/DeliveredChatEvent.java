package io.darbata.dispatcher.events.outgoing;

import io.darbata.dispatcher.models.Chat;

import java.util.List;

public record DeliveredChatEvent (
    Chat chat,
    List<String> recipients
) { }