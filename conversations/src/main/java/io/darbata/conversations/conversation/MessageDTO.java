package io.darbata.conversations.conversation;

import java.time.LocalDate;
import java.util.UUID;

public record MessageDTO(
        UUID messageId,
        long from,
        long to,
        String content,
        LocalDate createdAt
) { }