package io.darbata.conversations.conversation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
class DatabaseInit {

    private final JdbcClient client;
    private final Logger logger = LogManager.getLogger();

    DatabaseInit(JdbcClient client) {
        this.client = client;
    }

    @Bean // run this bean on startup
    CommandLineRunner initTables() {
        logger.info("INIT POSTGRES TABLES");
        return args -> {
            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY
                    );
                """
            ).update();

            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS conversations (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY
                    );
                """
            ).update();

            client.sql(
                """
                    CREATE TABLE IF NOT EXISTS user_conversations (
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        conversation_id BIGINT NOT NULL REFERENCES conversations(id),
                        joined_at TIMESTAMP DEFAULT NOW(),
                        last_message_at TIMESTAMP DEFAULT NOW(),
                        PRIMARY KEY (user_id, conversation_id)
                    );
                """
            ).update();
        };
    }
}