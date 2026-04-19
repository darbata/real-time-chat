CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY 
);

CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT PRIMARY KEY 
);

CREATE TABLE IF NOT EXISTS user_conversations (
    user_id TEXT NOT NULL REFERENCES users(id),
    conversation_id BIGINT NOT NULL REFERENCES conversations(id),
    joined_at TIMESTAMP DEFAULT NOW(),
    last_message_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (user_id, conversation_id)
);
