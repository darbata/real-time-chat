package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.dto.ConversationDTO;
import io.darbata.conversations.conversation.dto.MessageDTO;
import io.darbata.conversations.conversation.exceptions.NoConversationException;
import io.darbata.conversations.conversation.exceptions.UserNotFoundException;
import io.darbata.conversations.conversation.exceptions.UserNotInConversationException;
import io.darbata.conversations.conversation.models.User;
import org.springframework.dao.DataIntegrityViolationException;
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

    public ConversationDTO fetchConversationById(String userId, Long conversationId) {
        if (!isUserInConversation(userId, conversationId))
            throw new UserNotInConversationException("User not in conversation");

        return conversationRepository.findConversationById(conversationId).orElseThrow(
                () -> new NoConversationException("No conversation with id" + conversationId)
        );
    }

    public List<String> fetchConversationParticipantIds(String userId, Long conversationId) {
        if (!isUserInConversation(userId, conversationId))
            throw new UserNotInConversationException("User not in conversation");

        List<User> users = conversationRepository.findConversationParticipantsById(conversationId);

        return users.stream().map(User::username).toList();
    }

    public List<MessageDTO> fetchConversationMessages(String userId, Long conversationId, int limit, int offset) {
        // to be implemented with RestClient
        return null;
    }

    public ConversationDTO createConversation(String userId, List<String> participants) {
        // participants needs to include userId
        if (!participants.contains(userId)) participants.add(userId);

        try {
            return conversationRepository.createConversationWithParticipants(participants);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException("One or more of the participantIds are invalid");
        }
    }

    public void leaveConversation(String userId, Long conversationId) {
        if (!isUserInConversation(userId, conversationId))
            throw new UserNotInConversationException("User not in conversation");

        conversationRepository.removeUserFromConversation(userId, conversationId);
    }

    private boolean isUserInConversation(String userId, Long conversationId) {
        return this.conversationRepository.isUserInConversation(conversationId, userId);
    }

}