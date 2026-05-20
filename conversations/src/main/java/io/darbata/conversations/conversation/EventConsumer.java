package io.darbata.conversations.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);
    private final ConversationService conversationService;

    EventConsumer(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @RabbitListener(queues="recent-messages")
    public void onRecentMessage(RecentMessageEvent event) {
        log.info("received recent-messages {}", event);
        conversationService.setLastMessage(event.conversationId(), event.content());
    }


}
