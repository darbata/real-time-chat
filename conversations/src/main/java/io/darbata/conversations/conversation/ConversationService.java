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

    public List<ConversationDTO> fetchConversations(String userId, int limit, int offset) {
        int queryLimit = Math.min(10, limit);
        return conversationRepository.findRecentConversationsWithParticipants(userId, queryLimit, offset);
    }

    public List<MessageDTO> fetchConversationMessages(String userId, Long conversationId, int limit, int offset) {
        if (!isUserInConversation(userId, conversationId))
            throw new RuntimeException("User " + userId + "  not in conversation " + conversationId);

        int queryLimit = Math.min(24, limit);

        return messageRepository.fetchMessages(conversationId, queryLimit, null);
    }

    private boolean isUserInConversation(String userId, Long conversationId) {
        return this.conversationRepository.isUserInConversation(conversationId, userId);
    }

    public void setLastMessage(long conversationId, String content) {
        conversationRepository.updateConversationLastMessage(conversationId, content);
    }
}