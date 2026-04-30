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

    Optional<MessageDTO> fetchLastMessage(long conversationId) {
        List<MessageDTO> messages = fetchRecentMessages(conversationId, 1);
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.getFirst());
    }

    List<MessageDTO> fetchMessages(long conversationId, int limit, Ulid lastMessageId) {
        if (lastMessageId == null) {
            return fetchRecentMessages(conversationId, limit);
        } else {
            return fetchRecentMessagesAfter(conversationId, limit, lastMessageId);
        }
    }

    private List<MessageDTO> fetchRecentMessages(long conversationId, int limit) {
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

        return response.items().stream()
                .map(this::toMessageDTO)
                .toList();
    }

    private List<MessageDTO> fetchRecentMessagesAfter(long conversationId, int limit, Ulid lastMessageId) {
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

    private MessageDTO toMessageDTO(Map<String, AttributeValue> item) {
        return new MessageDTO (
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