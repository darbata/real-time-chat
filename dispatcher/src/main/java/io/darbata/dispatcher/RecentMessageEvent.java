package io.darbata.dispatcher;

public record RecentMessageEvent (
        long conversationId,
        String content
) { }
