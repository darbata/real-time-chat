package io.darbata.chat.message;

import java.util.Date;

public record MessageDTO (
    String conversationId,
    String id,
    long from,
    long to,
    String content,
    Date createdAt
) { }
