package io.darbata.dispatcher;

import io.darbata.dispatcher.models.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.util.*;

@Repository
public class DynamoDbKvStore implements KVStore {

    @Value("${aws.sdk.dynamo-db.table}")
    private String tableName;

    private final DynamoDbClient client;
    private final Logger logger = LoggerFactory.getLogger(DynamoDbKvStore.class);

    public DynamoDbKvStore(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void put(String partitionKey, String sortKey, Chat chat) {

        // create list of items
        HashMap<String, AttributeValue> itemValues = new HashMap<>();

        itemValues.put("conversationId", AttributeValue.builder().s(partitionKey).build());
        itemValues.put("chatId", AttributeValue.builder().s(sortKey).build());

        itemValues.put("content", AttributeValue.builder().s(chat.content()).build());
        itemValues.put("sender", AttributeValue.builder().s(chat.senderId()).build());
        itemValues.put("sentAt", AttributeValue.builder().s(chat.sentAt().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            client.putItem(request);
            logger.info("Stored chat: {}", chat);
        } catch (Exception e) {
            logger.error("Failed to store chat", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Chat> get(String partitionKey, Optional<String> beforeSortKey, int limit) {
        Map<String, AttributeValue> values = new HashMap<>();

        // partition key
        values.put(":conversationId", AttributeValue.builder().s(partitionKey).build());

        // sort key
        String key = "conversationId = :conversationId";
        if (beforeSortKey.isPresent()) {
            key += " AND chatId < :cursor";
            values.put(":cursor", AttributeValue.builder().s(beforeSortKey.get()).build());
        }

        QueryRequest query = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(key)
                .expressionAttributeValues(values)
                .scanIndexForward(false) // newest first
                .limit(limit)
                .build();

        try {
            QueryResponse response = client.query(query);
            return response.items().stream().map(this::toChat).toList();

        } catch (Exception e) {
            logger.error("Failed to query conversation {}", partitionKey, e);
            throw new RuntimeException(e);
        }
    }

    private Chat toChat(Map<String, AttributeValue> item) {
        return new Chat(
            item.get("chatId").s(),
            Long.parseLong(item.get("conversationId").s()),
            item.get("sender").s(),
            item.get("content").s(),
            Instant.parse(item.get("sentAt").s())
        );
    }

}