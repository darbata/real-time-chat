package io.darbata.dispatcher;

import com.github.f4b6a3.ulid.UlidCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Queue;

import java.time.Instant;
import java.util.List;

@Service
class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final AmqpTemplate template;
    private final ConversationRepository conversationRepository;
    private final Queue dispatchQueue;

    ChatService(AmqpTemplate template, ConversationRepository conversationRepository, Queue dispatchQueue) {
        this.template = template;
        this.conversationRepository = conversationRepository;
        this.dispatchQueue = dispatchQueue;
    }

    public void sendMessage(IncomingMessage message) {
        String senderId = message.senderId();

        List<String> recipients = conversationRepository.fetchRecipients(
                message.conversationId(),
                senderId
        );

        String messageId = createMessageId();

        DispatchMessageEvent dispatchMessageEvent = new DispatchMessageEvent(
                message.conversationId(),
                messageId,
                senderId,
                message.content(),
                recipients,
                Instant.now()
        );

        log.info("Dispatching Event: {}", dispatchMessageEvent);

        this.template.convertAndSend(dispatchQueue.getName(), dispatchMessageEvent);
    }

    private String createMessageId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}