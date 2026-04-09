package io.darbata.conversations.conversation;

import java.util.Date;
import java.util.UUID;

public record MessageDTO(
        UUID messageId,
        long from,
        long to,
        String content,
        Date createdAt
) { }