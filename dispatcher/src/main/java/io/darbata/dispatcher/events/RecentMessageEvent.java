package io.darbata.dispatcher.events;

public record RecentMessageEvent (
        long conversationId,
        String content
) { }
