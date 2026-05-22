package io.darbata.conversations.conversation.dto;

import java.util.List;
import java.util.Optional;

public record FetchedMessagesDTO (
    List<Chat> chats,
    Optional<String> before
) {};
