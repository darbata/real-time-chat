package io.darbata.conversations.conversation;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public InboxDTO fetchUserInbox(long userId, int conversationQueryLimit, int offset, int messageQueryLimit) {
        int queryLimit = Math.min(10, conversationQueryLimit);
        List<Long> conversationIds = conversationRepository.findRecentConversationIds(userId, queryLimit, offset);
        queryLimit = Math.min(50, messageQueryLimit);
        List<ConversationDTO> conversationsMessages = messageRepository.findConversationsMessages(conversationIds, queryLimit);
        return new InboxDTO(conversationsMessages);
    }

}