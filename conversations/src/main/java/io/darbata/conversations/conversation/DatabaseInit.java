package io.darbata.conversations.conversation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
class DatabaseInit {

    private final JdbcClient client;

    DatabaseInit(JdbcClient client) {
        this.client = client;
    }

    @Bean // run this bean on startup
    CommandLineRunner initTables() {
        return args -> {
            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT PRIMARY KEY
                    );
                """
            ).update();

            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS conversations (
                        id BIGINT PRIMARY KEY
                    );
                """
            ).update();

            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS user_conversations (
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        conversation_id BIGINT NOT NULL REFERENCES conversations(id),
                        joined_at TIMESTAMP DEFAULT NOW(),
                        PRIMARY KEY (user_id, conversation_id)
                    );
                """
            ).update();
        };
    }
}