package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.dto.ConversationDTO;
import io.darbata.conversations.conversation.models.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
class ConversationRepository {

    private final JdbcClient client;

    ConversationRepository(JdbcClient client) {
        this.client = client;
    }

    List<ConversationDTO> findRecentConversationsWithParticipants(String userId, int limit, int offset) {
        String query = """
            SELECT * FROM conversations_with_participants
            WHERE :userId = ANY(participant_ids)
            LIMIT :limit OFFSET :offset;
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

    public Optional<ConversationDTO> findConversationById(Long conversationId) {
        String query = """
            SELECT * FROM conversations_with_participants
            WHERE conversation_id = :conversationId
        """;

        return client.sql(query)
                .param("conversationId", conversationId)
                .query((rs, n) -> new ConversationDTO (
                        rs.getLong("conversation_id"),
                        Arrays.stream((String[]) rs.getArray("participant_ids").getArray())
                                .map(User::new)
                                .toList()
                ))
                .optional();
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

    public List<User> findConversationParticipantsById(Long conversationId) {
        String query = """
        SELECT u.id
        FROM users u
        JOIN user_conversations uc ON u.id = uc.user_id
        WHERE uc.conversation_id = :conversationId
        """;

        return client.sql(query)
            .param("conversationId", conversationId)
            .query((rs, n) -> new User(rs.getString("id")))
            .list();
    }

    public ConversationDTO createConversationWithParticipants(List<String> participantIds) {
        String query = """
        WITH new_conversation AS (
            INSERT INTO conversations DEFAULT VALUES
            RETURNING id
        ),
        inserted_participants AS (
            INSERT INTO user_conversations (user_id, conversation_id)
            SELECT unnest(CAST(:participantIds AS text[])), (SELECT id FROM new_conversation)
            RETURNING user_id, conversation_id
        )
        SELECT
            conversation_id,
            array_agg(user_id) AS participant_ids
        FROM inserted_participants
        GROUP BY conversation_id
        """;

        return client.sql(query)
                .param("participantIds", participantIds.toArray(new String[0]))
                .query((rs, n) -> new ConversationDTO(
                        rs.getLong("conversation_id"),
                        Arrays.stream((String[]) rs.getArray("participant_ids").getArray())
                                .map(User::new)
                                .toList()
                ))
                .single();
    }

    public void removeUserFromConversation(String userId, Long conversationId) {
        String query = """
            DELETE FROM user_conversations
            WHERE user_id = :userId
            AND conversation_id = :conversationId
        """;

        client.sql(query)
                .param("userId", userId)
                .param("conversationId", conversationId)
                .update();
    }
}