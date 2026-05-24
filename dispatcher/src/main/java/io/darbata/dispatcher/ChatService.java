package io.darbata.dispatcher;

import io.darbata.dispatcher.exceptions.ChatNotFoundException;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.models.ChatStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public MessagesDTO fetchConversationMessages(Long conversationId, Optional<String> before, int limit) {
        List<Chat> chats = chatRepository.fetchMessagesByConversationId(Long.toString(conversationId), before, limit);
        Optional<String> oldestChatId = Optional.empty();
        if (!chats.isEmpty()) {
            oldestChatId = Optional.of(chats.getLast().id());
        }
        return new MessagesDTO(chats, oldestChatId);
    }

    public void updateMessage(String updaterId, long conversationId, String messageId, Chat updated) {
        Chat chat = chatRepository.fetchMessage(Long.toString(conversationId), messageId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.senderId().equals(updaterId)) throw new IllegalCallerException("User can't update this chat");

        Chat updatedChat = new Chat(
            chat.id(),
            chat.conversationId(),
            chat.senderId(),
            updated.content(),
            chat.sentAt(),
            updated.status()
        );

        chatRepository.update(conversationId, messageId, updatedChat);

    }

    public void deleteMessage(long conversationId, String messageId) {
        chatRepository.deleteMessage(conversationId, messageId);
    }
}