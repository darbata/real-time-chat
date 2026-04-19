package io.darbata.dispatcher;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class ChatRepository {

    private final KVStore store;

    ChatRepository(KVStore store) {
        this.store = store;
    }

    Chat save(IncomingChat incomingChat) {
        String id = createChatId();
        Chat chat = new Chat(id, incomingChat.conversationId(), incomingChat.senderId(), incomingChat.content(),
                Instant.now());
        store.put(
                Long.toString(chat.conversationId()),
                id,
                chat
        );
        return chat;

    }
    private String createChatId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}


