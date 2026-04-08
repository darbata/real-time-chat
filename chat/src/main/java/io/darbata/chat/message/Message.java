package io.darbata.chat.message;

import java.util.Date;
import com.github.f4b6a3.ulid.Ulid;

public class Message {
    // Ulid uses timestamps, so monotonically increasing
    String conversationId;
    private Ulid id;
    private long from;
    private long to;
    private String content;
    private Date createdAt;

    private Message() {}

    public static Message draft(long from, long to, String content) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setContent(content);
        return message;
    }

    public static Message create(String conversationId, Ulid id, long from, long to, String content, Date createdAt) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setId(id);
        message.setFrom(from);
        message.setTo(to);
        message.setContent(content);
        message.setCreatedAt(createdAt);
        return message;
    }

    public String getConversationId() {
        return conversationId;
    }

    private void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Ulid getId() {
        return id;
    }

    private void setId(Ulid id) {
        this.id = id;
    }

    public long getFrom() {
        return from;
    }

    private void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    private void setTo(long to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    private void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}