package io.darbata.dispatcher;

import io.darbata.dispatcher.exceptions.ChatNotFoundException;
import io.darbata.dispatcher.models.Chat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatAmqpService chatAmqpService;

    public ChatService(ChatRepository chatRepository, ChatAmqpService chatAmqpService) {
        this.chatRepository = chatRepository;
        this.chatAmqpService = chatAmqpService;
    }

    public MessagesDTO fetchConversationMessages(Long conversationId, Optional<String> before, int limit) {
        List<Chat> chats = chatRepository.fetchMessagesByConversationId(Long.toString(conversationId), before, limit);
        Optional<String> oldestChatId = Optional.empty();
        if (!chats.isEmpty()) {
            oldestChatId = Optional.of(chats.getLast().id());
        }
        return new MessagesDTO(chats, oldestChatId);
    }

    public void updateMessageContent(long conversationId, String messageId, String updatedMessageContent) {
        Chat chat = chatRepository.fetchMessage(Long.toString(conversationId), messageId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));


        Chat updatedChat = new Chat(
                chat.id(),
                chat.conversationId(),
                chat.senderId(),
                updatedMessageContent,
                chat.sentAt(),
                chat.status()
        );

        chatRepository.update(conversationId, messageId, updatedChat);
    }

    public Chat createChat(long conversationId, String senderId, String content) {
        Chat chat = chatRepository.save(conversationId, senderId, content);
        chatAmqpService.produceDeliveredChatEvent(chat);
        return chat;
    }



    public void deleteMessage(long conversationId, String messageId) {
        chatRepository.deleteMessage(conversationId, messageId);
    }
}