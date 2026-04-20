package io.darbata.chat.message;

import java.util.Date;

record ChatDTO (
        long conversationId,
        String chatId,
        String senderId,
        String content,
        Date sentAt
) { }