package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.http.HttpHeaders;

@Controller
class MessageController {

    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    void handle(@Payload SendMessageDTO chat, @Header("X-User-Id") String senderId) {
        logger.info("User {} sending {}", senderId, chat);
        messageService.sendMessage(chat.conversationId(), senderId, chat.content());
    }

}