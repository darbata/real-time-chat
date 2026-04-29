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
    PRIMARY KEY (user_id, conversation_id)
);

CREATE VIEW conversation_with_participants AS
SELECT
    conversations.id,
    array_agg(users.id) AS participant_ids
FROM conversations
JOIN user_conversations ON user_conversations.conversation_id = conversations.id
JOIN users on users.id = user_conversations.user_id
GROUP BY conversations.id;
