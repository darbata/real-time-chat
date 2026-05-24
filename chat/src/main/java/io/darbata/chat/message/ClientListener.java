package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
class ClientListener {

    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(ClientListener.class);

    ClientListener(MessageService messageService) {
        this.messageService = messageService;
    }

    // ingress, clients will send to this topic
    @MessageMapping("/chat")
    void handleUserSentChatEvent(@Payload SendMessageDTO chat, @Header("X-User-Id") String senderId) {
        logger.info("User {} sending {}", senderId, chat);
        messageService.sendMessage(chat.conversationId(), senderId, chat.content());
    }

    @MessageMapping("/typing")
    void handleUserTypingEvent() {
        // fan out to all participants in conversation user began typing
    }


    @MessageMapping("/read")
    void handleUserReadChatEvent() {
        // fan out to all participants in conversation user began typing
    }
}