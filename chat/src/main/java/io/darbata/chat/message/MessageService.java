package io.darbata.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messageTemplate;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messageTemplate) {
        this.messageRepository = messageRepository;
        this.messageTemplate = messageTemplate;
    }

    public void sendMessage(long from, long to, String content) {
        Message message = Message.draft(from, to, content);

        // save message to KV store
        Message saved = messageRepository.save(message);

        logger.debug("save message: {}", saved);
        logger.debug(saved.getId().toString());

        MessageDTO dto = new MessageDTO(
                saved.getConversationId(),
                saved.getId().toString(),
                saved.getFrom(),
                saved.getTo(),
                saved.getContent(),
                saved.getCreatedAt()
        );

        // send message with id to user-specific queue
        messageTemplate.convertAndSendToUser(Long.toString(to), "/chats", dto);
    }
}