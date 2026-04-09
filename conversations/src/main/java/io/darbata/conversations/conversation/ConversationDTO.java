package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.MessageDTO;

import java.util.List;

public record ConversationDTO(
        long id,
        List<MessageDTO> messages
) { }