package io.darbata.chat.message;

import java.util.Date;

record MessageDTO (
        long conversationId,
        String messageId,
        String senderId,
        String content,
        Date sentAt
) { }