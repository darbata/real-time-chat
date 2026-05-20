package io.darbata.conversations.conversation;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import javax.print.DocFlavor;
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
            JOIN user_conversations ON
                user_conversations.conversation_id = conversations_with_participants.conversation_id
            WHERE user_conversations.user_id = :userId
            ORDER BY user_conversations.last_message_at DESC
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
                                .toList(),
                        rs.getString("last_message"),
                        rs.getString("last_message_at")
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

    public void updateConversationLastMessage(long conversationId, String content) {
        String query = """
            UPDATE user_conversations
            SET last_message = :content
            WHERE conversation_id = :conversationId;
        """;

        client.sql(query)
                .param("content", content)
                .param("conversationId", conversationId)
                .update();
    }
}