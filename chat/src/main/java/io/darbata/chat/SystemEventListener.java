package io.darbata.chat;

import io.darbata.chat.events.CreateConversationEvent;
import io.darbata.chat.events.DeliveredChatEvent;
import io.darbata.chat.events.DispatchUserReadChatEvent;
import io.darbata.chat.events.DispatchUserStartedTypingEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
class SystemEventListener {

    private final SimpMessagingTemplate template;

    SystemEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = "chat.delivered")
    void consumeDispatchEvent(DeliveredChatEvent event) {
        for (String recipient : event.recipients()) {
            // cluster node delegation auto-handled by the config
            template.convertAndSendToUser(
                recipient,
                "/queue/chats.delivered",
                event
            );
        }
    }

    @RabbitListener(queues = "chat.typing.out")
    void consumeTypingEvent(DispatchUserStartedTypingEvent event) {
        for (String recipient : event.recipients()) {
            template.convertAndSendToUser(
                recipient,
                "/queue/chats.typing",
                event
            );
        }
    }

    @RabbitListener(queues = "chat.read.out")
    void consumeReadEvent(DispatchUserReadChatEvent event) {
        for (String recipient : event.recipients()) {
            template.convertAndSendToUser(
                recipient,
                "/queue/chats.read",
                event
            );
        }
    }
    
    @RabbitListener(queues = "conversation.created")
    void consumeCreateConversationEvent(CreateConversationEvent event) {
        for (String recipient : event.recipients()) {
            template.convertAndSendToUser(
                recipient,
                "/queue/conversation.created",
                event
            );
        }
    }
}