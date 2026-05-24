package io.darbata.chat;

import io.darbata.chat.events.UserReadChatEvent;
import io.darbata.chat.events.UserSentChatEvent;
import io.darbata.chat.events.UserStartedTypingEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
class ClientListener {

    private final RabbitTemplate template;

    ClientListener(RabbitTemplate template) {
        this.template = template;
    }

    // ingress, clients will send to this topic
    @MessageMapping("/chat")
    void handleUserSentChatEvent(@Payload UserSentChatEvent event) {
        template.convertAndSend("chat.sent", event);
    }

    @MessageMapping("/typing")
    void handleUserTypingEvent(@Payload UserStartedTypingEvent event) {
        template.convertAndSend("chat.typing.in", event);
    }

    @MessageMapping("/read")
    void handleUserReadChatEvent(@Payload UserReadChatEvent event) {
        template.convertAndSend("chat.read.in", event);
    }
}