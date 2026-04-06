package io.darbata.chat.message;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import io.darbata.chat.persistence.KVStore;
import io.darbata.chat.persistence.LocalKVStore;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public class MessageRepository {

    private final KVStore store;

    public MessageRepository(LocalKVStore store) {
        this.store = store;
    }

    public Message save(long conversationId, Message message) {
        Ulid messageId = getNextId();
        Date messageCreatedAt = Date.from(Instant.now());

        Message saved = Message.load(
                messageId, message.getFrom(), message.getTo(), message.getContent(), messageCreatedAt
        );

        store.put(conversationId, saved);

        return saved;
    }

    public List<Message> loadConversation(long conversationId) {
        return store.get(conversationId);
    }

    private Ulid getNextId() {
        // always increasing based on time
        // all later messages will have a 'later' Ulid
        return UlidCreator.getMonotonicUlid();
    }

}