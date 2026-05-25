package io.darbata.conversations.dto;

import java.util.List;

public record CreateConversationRequestDTO(
        List<String> participantIds
) { }