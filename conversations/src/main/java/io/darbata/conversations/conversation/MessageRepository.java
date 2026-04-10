package io.darbata.conversations.conversation;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

@Repository
class MessageRepository {

    @Value("${aws.sdk.dynamo-db.table}")
    private String table;

    private final DynamoDbClient client;

    MessageRepository(DynamoDbClient client) {
        this.client = client;
    }

    List<MessageDTO> findConversationMessage(long conversationId, int limit, Ulid lastMessageId) {
        QueryRequest request = QueryRequest.builder()
                .tableName(table)
                .keyConditionExpression("pk = :pk AND sk < :sk")
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.builder().s("CONV#" + conversationId).build(),
                        ":sk", AttributeValue.builder().s("MSG#" + lastMessageId).build()
                ))
                .scanIndexForward(false)
                .limit(limit)
                .build();

        QueryResponse response = client.query(request);

        return response.items().stream()
                .map(this::toMessageDTO)
                .toList();
    }

    List<ConversationDTO> findConversationsMessages(List<Long> conversationIds, int limit) {
        return conversationIds.stream()
                .map(id -> queryConversation(id, limit))
                .toList();
    }

    private ConversationDTO queryConversation(long conversationId, int limit) {
        QueryRequest request = QueryRequest.builder()
                .tableName(table)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.builder().s("CONV#" + conversationId).build()
                ))
                .scanIndexForward(false)
                .limit(limit)
                .build();

        QueryResponse response = client.query(request);

        List<MessageDTO> messages = response.items().stream()
                .map(this::toMessageDTO)
                .toList();

        return new ConversationDTO(conversationId, messages);
    }

    private MessageDTO toMessageDTO(Map<String, AttributeValue> item) {
        return new MessageDTO(
            UUID.fromString(item.get("messageId").s()),
            Long.parseLong(item.get("from").n()),
            Long.parseLong(item.get("to").n()),
            item.get("content").s(),
                Instant.ofEpochMilli(Long.parseLong(item.get("createdAt").n()))
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
        );
    }
}