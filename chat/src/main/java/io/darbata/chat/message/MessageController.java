package io.darbata.chat.message;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
class MessageController {
    private final MessageService messageService;

    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    void handle(long userId, SendMessageDTO chat) {
        messageService.sendMessage(chat.conversationId(), userId, chat.to(), chat.content());
    }
}