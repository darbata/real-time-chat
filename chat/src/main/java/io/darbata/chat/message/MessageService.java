package io.darbata.chat.message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messageTemplate;

    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messageTemplate) {
        this.messageRepository = messageRepository;
        this.messageTemplate = messageTemplate;
    }

    public void sendMessage(long from, long to, String content) {
        Message message = Message.draft(from, to, content);

        // save message to KV store
        Message saved = messageRepository.save(message);

        // send message with id to user-specific queue
        messageTemplate.convertAndSendToUser(Long.toString(to), "/chats", saved);
    }
}