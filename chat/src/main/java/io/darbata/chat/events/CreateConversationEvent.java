package io.darbata.chat.events;

import io.darbata.chat.models.ConversationDTO;

import java.util.List;

public record CreateConversationEvent (
        ConversationDTO conversation ,
        List<String> recipients
) {}