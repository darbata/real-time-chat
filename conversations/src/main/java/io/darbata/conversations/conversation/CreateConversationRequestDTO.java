package io.darbata.conversations.conversation;

import java.util.List;

public record CreateConversatoinRequestDTO(
        List<User> particiapnts,
        String firstMessage
) { }
