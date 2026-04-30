package io.darbata.conversations.conversation;

import java.util.List;

public record ConversationDTO (
        long id,
        List<User> participants,
        String lastMessage,
        String lastMessageAt // timestamp
) { }