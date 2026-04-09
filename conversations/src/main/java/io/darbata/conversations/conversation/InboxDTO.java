package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.ConversationDTO;

import java.util.List;

public record InboxDTO (
    List<ConversationDTO> conversations
) {}