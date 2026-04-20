package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
class MessageController {

    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    void handle(@Payload SendMessageDTO chat) {
        logger.info("User {} sending {}", chat.userId(), chat);
        messageService.sendMessage(chat.conversationId(), chat.userId(), chat.content());
    }

}