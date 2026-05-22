package io.darbata.dispatcher;

import io.darbata.dispatcher.models.Chat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public MessagesDTO fetchConversationMessages(Long conversationId, Optional<String> before, int limit) {
        List<Chat> chats = chatRepository.fetchMessageByConversationId(Long.toString(conversationId), before, limit);
        Optional<String> oldestChatId = Optional.empty();
        if (!chats.isEmpty()) {
            oldestChatId = Optional.of(chats.getLast().id());
        }
        return new MessagesDTO(chats, oldestChatId);
    }

    public void updateMessage(long conversationId, String messageId, String updatedMessageContent) {
        chatRepository.updateMessage(conversationId, messageId, updatedMessageContent);
    }

    public void deleteMessage(long conversationId, String messageId) {
        chatRepository.deleteMessage(conversationId, messageId);
    }
}