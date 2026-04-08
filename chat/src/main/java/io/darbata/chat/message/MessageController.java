package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

@Controller
class MessageController {

    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    void handle(@AuthenticationPrincipal Jwt jwt, @Payload SendMessageDTO chat) {
        long id = Long.parseLong(jwt.getClaimAsString("id"));
        logger.info("User {} sending {}", id, chat);
        messageService.sendMessage(
                id, // from this user
                chat.to(),
                chat.content()
        );
    }
}