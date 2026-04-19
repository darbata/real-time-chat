package io.darbata.chat.message;

import java.util.Date;

record MessageDTO (
        long conversationId,
        String messageId,
        String userId,
        String content,
        Date sentAt
) { }