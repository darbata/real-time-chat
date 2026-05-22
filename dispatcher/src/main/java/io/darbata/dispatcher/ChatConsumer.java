package io.darbata.dispatcher;

import io.darbata.dispatcher.models.IncomingChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatConsumer {

    private final Logger log = LoggerFactory.getLogger(ChatConsumer.class);
    private final ChatAmqpService chatService;

    ChatConsumer(ChatAmqpService chatService) {
        this.chatService = chatService;
    }

    @RabbitListener(queues="chats")
    public void onMessage(IncomingChat message) {
        log.info("Receiving message: {}", message);
        chatService.consumeIncomingMessage(message);
    }
}