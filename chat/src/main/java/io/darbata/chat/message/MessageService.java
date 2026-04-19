package io.darbata.chat.message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
class MessageService {
    private final SimpMessagingTemplate messageTemplate;

    public MessageService(SimpMessagingTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public void sendMessage(long conversationId, String from, String content) {
        IncomingMessage message = new IncomingMessage(conversationId, from, content);

        // send message with id to user-specific queue
        // send `dto` to /user/{to}/queue/chats
        messageTemplate.convertAndSend("/queue/chats", message);
    }
}