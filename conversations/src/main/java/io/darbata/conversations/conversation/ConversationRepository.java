package io.darbata.conversations.conversation;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
class ConversationRepository {

    private final JdbcClient client;

    ConversationRepository(JdbcClient client) {
        this.client = client;
    }

    List<Long> findRecentConversationIds(long userId, int limit, int offset) {
        String query = """
            SELECT conversation_id FROM user_conversations 
            WHERE user_id = :userId 
            ORDER BY last_message_at DESC
            LIMIT :limit OFFSET :offset;
        """;

        return client.sql(query)
                .param("userId", userId)
                .param("limit", limit)
                .param("offset", offset)
                .query(Long.class)
                .list();
    }

    void updateLastMessageAt(long conversationId) {
        String query = """
            UPDATE user_conversations 
            SET last_message_at = NOW() 
            WHERE conversation_id = :conversationId;
        """;

        client.sql(query)
            .param("conversationId", conversationId)
            .update();
    }

    @Transactional
    void createUserConversation(List<Long> userIds) {
        String createConversationQuery = """
            INSERT INTO conversations (id) 
            VALUES (DEFAULT) RETURNING id;
        """;

        Long conversationId = client.sql(createConversationQuery)
            .query(Long.class)
            .single();

        String addUserQuery = """
                INSERT INTO user_conversations (conversation_id, user_id)
                VALUES (:conversationId, :userId);
        """;

        for (Long id : userIds) {
            client.sql(addUserQuery)
                    .param("conversationId", conversationId)
                    .param("userId", id)
                    .update();
        }
   }
}