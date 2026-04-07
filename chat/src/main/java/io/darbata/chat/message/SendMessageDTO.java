package io.darbata.chat.message;

record SendMessageDTO (
    long conversationId,
    long to,
    String content
) {
    @Override
    public String toString() {
        return "SendMessageDTO{" +
                "conversationId=" + conversationId +
                ", to=" + to +
                ", content='" + content + '\'' +
                '}';
    }
};