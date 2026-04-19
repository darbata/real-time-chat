package io.darbata.dispatcher;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class ConversationRepository {

    final JdbcClient jdbcClient;

    ConversationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<String> fetchRecipients(long conversationId, String senderId) {
        String query = """
            SELECT u.id FROM users u
            INNER JOIN user_conversations uc ON u.id = uc.user_id
            WHERE uc.conversation_id = :conversationId
            AND u.id != :senderId;
        """;

        return jdbcClient.sql(query)
                .param("conversationId", conversationId)
                .param("senderId", senderId)
                .query(String.class)
                .list();
    }

}