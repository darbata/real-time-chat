package io.darbata.chat.message;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import io.darbata.chat.persistence.KVStore;
import io.darbata.chat.persistence.LocalKVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public class MessageRepository {

    private final KVStore store;
    private final Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    public MessageRepository(LocalKVStore store) {
        this.store = store;
    }

    public Message save(Message draft) {
        Ulid messageId = getNextId();
        Date messageCreatedAt = Date.from(Instant.now());
        String conversationId = buildConversationId(draft.getFrom(), draft.getTo());

        Message message = Message.create(
               conversationId,  messageId, draft.getFrom(), draft.getTo(), draft.getContent(), messageCreatedAt
        );

        store.put(
            buildKey(conversationId, messageId),
            message
        );

        return message;
    }

    private Ulid getNextId() {
        // always increasing based on time
        // all later messages will have a 'later' Ulid
        return UlidCreator.getMonotonicUlid();
    }

    private String buildConversationId(long from, long to) {
        // deterministic, no matter who is from or to the conversationid is the same
        return "conversation-" + Math.min(from, to) + Math.max(from, to);
    }

    private String buildKey(String conversationId, Ulid messageId) {
        String key = conversationId + "#" + messageId.toString();
        logger.info("Created key: " + key);
        return key;
    }

}