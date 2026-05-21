package io.darbata.conversations.conversation.dto;

import java.util.List;

public record CreateConversationRequestDTO(
        List<String> participantIds
) { }
