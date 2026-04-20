package io.darbata.chat.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
class MessageService {

    private final RabbitTemplate rabbitTemplate;

    public MessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(long conversationId, String from, String content) {
        IncomingMessage message = new IncomingMessage(conversationId, from, content);

        // send message with id to user-specific queue
        // send `dto` to /user/{to}/queue/chats
        rabbitTemplate.convertAndSend("chats", message);
    }
}