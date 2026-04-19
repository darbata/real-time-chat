package io.darbata.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Queue;

import java.util.List;

@Service
class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final AmqpTemplate template;
    private final ConversationRepository conversationRepository;
    private final Queue dispatchQueue;
    private final ChatRepository chatRepository;

    ChatService(AmqpTemplate template, ConversationRepository conversationRepository, Queue dispatchQueue,
                ChatRepository chatRepository) {
        this.template = template;
        this.conversationRepository = conversationRepository;
        this.dispatchQueue = dispatchQueue;
        this.chatRepository = chatRepository;
    }

    public void consumeIncomingMessage(IncomingChat incomingChat) {
        String senderId = incomingChat.senderId();

        List<String> recipients = conversationRepository.fetchRecipients(
                incomingChat.conversationId(),
                senderId
        );
        Chat chat = chatRepository.save(incomingChat);

        DispatchChatEvent dispatchChatEvent = new DispatchChatEvent(
                chat.conversationId(),
                chat.id(),
                senderId,
                chat.content(),
                recipients,
                chat.sentAt()
        );

        this.template.convertAndSend(dispatchQueue.getName(), dispatchChatEvent);

        log.info("Dispatching Event: {}", dispatchChatEvent);
    }
}