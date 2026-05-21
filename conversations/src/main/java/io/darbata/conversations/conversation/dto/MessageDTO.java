package io.darbata.conversations.conversation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MessageDTO (
        Long conversationId,
        UUID messageId,
        long from,
        long to,
        String content,
        LocalDate createdAt
) { }