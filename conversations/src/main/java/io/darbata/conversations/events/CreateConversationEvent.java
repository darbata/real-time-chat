package io.darbata.conversations.events;

import io.darbata.conversations.dto.ConversationDTO;

import java.util.List;

public record CreateConversationEvent (
       ConversationDTO conversation ,
       List<String> recipients
) {}