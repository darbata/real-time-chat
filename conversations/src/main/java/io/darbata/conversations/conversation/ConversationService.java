package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.dto.ConversationDTO;
import io.darbata.conversations.conversation.dto.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public List<ConversationDTO> fetchConversations(String userId, int limit, int offset) {
        int queryLimit = Math.min(10, limit);
        return conversationRepository.findRecentConversationsWithParticipants(userId, queryLimit, offset);
    }

    public List<MessageDTO> fetchConversationMessages(String userId, Long conversationId, int limit, int offset) {
        // to be implemented with RestClient
        return null;
    }

    private boolean isUserInConversation(String userId, Long conversationId) {
        return this.conversationRepository.isUserInConversation(conversationId, userId);
    }

}