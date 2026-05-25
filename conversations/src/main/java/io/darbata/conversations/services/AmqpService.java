package io.darbata.conversations.services;

import io.darbata.conversations.dto.ConversationDTO;
import io.darbata.conversations.events.CreateConversationEvent;
import io.darbata.conversations.models.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmqpService {

    private final AmqpTemplate template;

    public AmqpService(AmqpTemplate template) {
        this.template = template;
    }

    public void produceCreateConversationEvent(String creatorId, ConversationDTO conversation) {
        List<String> recipients = conversation.participants().stream()
                .map(User::username)
                .filter(username -> !username.equals(creatorId))
                .toList();
        var event = new CreateConversationEvent(conversation, recipients);
        this.template.convertAndSend("conversation.created", event);
    }

}