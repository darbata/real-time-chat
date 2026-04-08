package io.darbata.chat.message;

record SendMessageDTO (
    long to,
    String content
) {
    @Override
    public String toString() {
        return "SendMessageDTO{" +
                ", to=" + to +
                ", content='" + content + '\'' +
                '}';
    }
};