package io.darbata.dispatcher;

import com.github.f4b6a3.ulid.UlidCreator;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.models.IncomingChat;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    List<Chat> fetchMessageByConversationId(String conversationId, Optional<String> before, int limit) {
        return store.get(conversationId, before, limit);
    }

    private String createChatId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}


