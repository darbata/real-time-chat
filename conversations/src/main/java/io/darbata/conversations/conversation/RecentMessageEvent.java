package io.darbata.conversations.conversation;

public record RecentMessageEvent(
    long conversationId,
    String content
) { }
