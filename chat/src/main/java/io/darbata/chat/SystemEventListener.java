package io.darbata.chat;

import io.darbata.chat.events.DeliveredChatEvent;
import io.darbata.chat.events.DispatchUserReadChatEvent;
import io.darbata.chat.events.DispatchUserStartedTypingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
class SystemEventListener {

    private final SimpMessagingTemplate template;
    private final Logger log = LoggerFactory.getLogger(SystemEventListener.class);

    SystemEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = "chat.delivered")
    void consumeDispatchEvent(DeliveredChatEvent event) {
        // fans out sent messages
        // also sends to the original sender of the message which would indicate 'delivered'

        try {
            for (String recipient : event.recipients()) {
                // cluster node delegation auto-handled by the config
                template.convertAndSendToUser(
                    recipient,
                    "/queue/chats/delivered",
                    event
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "chat.typing.out")
    void consumeTypingEvent(DispatchUserStartedTypingEvent event) {
        for (String recipient : event.recipients()) {
            template.convertAndSendToUser(
                recipient,
                "/queue/chats/typing",
                event
            );
        }
    }

    @RabbitListener(queues = "chat.read.out")
    void consumeReadEvent(DispatchUserReadChatEvent event) {
        for (String recipient : event.recipients()) {
            template.convertAndSendToUser(
                recipient,
                "/queue/chats/read",
                event
            );
        }
    }
}