package io.darbata.dispatcher;

import com.github.f4b6a3.ulid.UlidCreator;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.models.ChatStatus;
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

    public Chat save(Long conversationId, String senderId, String content) {

        String id = createChatId();
        Chat chat = new Chat(id, conversationId, senderId, content, Instant.now(), ChatStatus.DELIVERED);

        store.put(
                Long.toString(chat.conversationId()),
                id,
                chat
        );

        return chat;

    }

    public void update(long conversationId, String messageId, Chat updated) {
        store.put(
                Long.toString(conversationId),
                messageId,
                updated
        );
    }

    public void deleteMessage(long conversationId, String messageId) {
        store.delete(Long.toString(conversationId), messageId);
    }

    public List<Chat> fetchMessagesByConversationId(String conversationId, Optional<String> before, int limit) {
        return store.getMessages(conversationId, before, limit);
    }

    public Optional<Chat> fetchMessage(String conversationId, String messageId) {
        return store.getMessage(conversationId, messageId);

    }

    private String createChatId() {
        return UlidCreator.getMonotonicUlid().toString();
    }

}


