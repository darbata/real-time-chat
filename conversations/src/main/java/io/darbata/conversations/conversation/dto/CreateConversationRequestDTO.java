package io.darbata.conversations.conversation.dto;

import io.darbata.conversations.conversation.models.User;

import java.util.List;

public record CreateConversationRequestDTO(
        List<User> participants,
        String firstMessage
) { }
