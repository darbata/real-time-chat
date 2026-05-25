package io.darbata.chat.models;

import java.util.List;

public record ConversationDTO (
        long id,
        List<User> participants
) { }