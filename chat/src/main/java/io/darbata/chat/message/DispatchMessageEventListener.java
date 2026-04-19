package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
class DispatchMessageEventListener {

    private final SimpMessagingTemplate template;
    private final Logger log = LoggerFactory.getLogger(DispatchMessageEventListener.class);

    DispatchMessageEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = "dispatch")
    void consumeDispatchEvent(DispatchMessageEvent event) {

        try {
            log.info("Receive dispatch event: {}", event);

            Date date = Date.from(event.sentAt());

            MessageDTO dto = new MessageDTO(
                    event.conversationId(),
                    event.messageId(),
                    event.userId(), // sender id
                    event.content(),
                    date
            );

            log.info("Sending to {}", event.userId());
            template.convertAndSendToUser(
                    event.userId(),
                    "/queue/chats",
                    dto
            );

            for (String recipient : event.recipients()) {
                // cluster node delegation auto-handled by the config
                template.convertAndSendToUser(
                        recipient,
                        "/queue/chats",
                        dto
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }



    }

}