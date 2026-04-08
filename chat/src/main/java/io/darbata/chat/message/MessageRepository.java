package io.darbata.chat.message;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import io.darbata.chat.persistence.DynamoDbKvStore;
import io.darbata.chat.persistence.KVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;

@Repository
public class MessageRepository {

    private final KVStore store;
    private final Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    public MessageRepository(DynamoDbKvStore store) {
        this.store = store;
    }

    public Message save(Message draft) {

        Ulid messageId = getNextId();
        Date messageCreatedAt = Date.from(Instant.now());

        String partitionKey = buildPartitionKey(draft.getFrom(), draft.getTo());
        String sortKey = buildSortKey(messageId);

        Message message = Message.create(
                partitionKey, messageId, draft.getFrom(), draft.getTo(), draft.getContent(), messageCreatedAt
        );

        store.put(
            partitionKey,
            sortKey,
            message
        );

        return message;
    }

    private Ulid getNextId() {
        // always increasing based on time
        // all later messages will have a 'later' Ulid
        return UlidCreator.getMonotonicUlid();
    }

    private String buildPartitionKey(long from, long to) {
        // deterministic, no matter who is from or to the conversationid is the same
        return "CONV#" + Math.min(from, to) + "#" + Math.max(from, to);
    }

    private String buildSortKey(Ulid messageId) {
        return "MSG#" + messageId.toString();
    }

}