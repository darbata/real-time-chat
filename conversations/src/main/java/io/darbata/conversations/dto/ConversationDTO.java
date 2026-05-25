package io.darbata.conversations.dto;

import io.darbata.conversations.models.User;

import java.util.List;

public record ConversationDTO (
        long id,
        List<User> participants
) { }