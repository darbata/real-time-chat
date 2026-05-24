package io.darbata.dispatcher;

import io.darbata.dispatcher.models.Chat;

import java.util.List;
import java.util.Optional;

public record MessagesDTO (
    List<Chat> chats,
    Optional<String> before
) {}