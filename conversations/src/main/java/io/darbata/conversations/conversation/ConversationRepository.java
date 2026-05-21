package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.dto.ConversationDTO;
import io.darbata.conversations.conversation.models.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
class ConversationRepository {

    private final JdbcClient client;

    ConversationRepository(JdbcClient client) {
        this.client = client;
    }

    List<ConversationDTO> findRecentConversationsWithParticipants(String userId, int limit, int offset) {
        String query = """
            SELECT * FROM conversations_with_participants
            JOIN user_conversations ON
                user_conversations.conversation_id = conversations_with_participants.conversation_id
            WHERE user_conversations.user_id = :userId
            LIMIT :limit OFFSET :offset
        """;

        return client.sql(query)
                .param("userId", userId)
                .param("limit", limit)
                .param("offset", offset)
                .query((rs, n) -> new ConversationDTO (
                        rs.getLong("conversation_id"),
                        Arrays.stream((String[]) rs.getArray("participant_ids").getArray())
                                .map(User::new)
                                .toList()
                ))
                .list();
    }

    public boolean isUserInConversation(Long conversationId, String userId) {
        String query = """
            SELECT 1 FROM user_conversations
            WHERE conversation_id = :conversationId
            AND user_id = :userId;
        """;

        return client.sql(query)
                .param("conversationId", conversationId)
                .param("userId", userId)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

}