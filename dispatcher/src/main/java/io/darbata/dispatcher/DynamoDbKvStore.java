package io.darbata.dispatcher;

import io.darbata.dispatcher.exceptions.ChatNotFoundException;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.models.ChatStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

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
        itemValues.put("status", AttributeValue.builder().s(chat.status().name()).build());

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
    public void delete(String partitionKey, String sortKey) {
        Map<String, AttributeValue> key = Map.of(
            "conversationId", AttributeValue.builder().s(partitionKey).build(),
            "chatId",         AttributeValue.builder().s(sortKey).build()
        );

        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .conditionExpression("attribute_exists(chatId)")
            .build();

        try {
            client.deleteItem(request);
            logger.info("Deleted chat {} in conversation {}", sortKey, partitionKey);
        } catch (ConditionalCheckFailedException e) {
            throw new ChatNotFoundException("Chat not found");
        } catch (Exception e) {
            logger.error("Failed to delete chat {} in conversation {}", sortKey, partitionKey, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Chat> getMessages(String partitionKey, Optional<String> beforeSortKey, int limit) {
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

    @Override
    public Optional<Chat> getMessage(String partitionKey, String sortKey) {
        Map<String, AttributeValue> key = Map.of(
                "conversationId", AttributeValue.builder().s(partitionKey).build(),
                "chatId",         AttributeValue.builder().s(sortKey).build()
        );

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        try {
            GetItemResponse response = client.getItem(request);
            return response.hasItem() ? Optional.of(toChat(response.item())) : Optional.empty();
        } catch (DynamoDbException e) {
            logger.error("Failed to get message {}/{}", partitionKey, sortKey, e);
            throw new ChatNotFoundException("Chat not found");
        }
    }

    @Override
    public boolean exists(String partitionKey, String sortKey) {
        Map<String, AttributeValue> key = Map.of(
            "conversationId", AttributeValue.builder().s(partitionKey).build(),
            "chatId", AttributeValue.builder().s(sortKey).build()
        );

        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .projectionExpression("chatId") // just need the one property
            .build();
        GetItemResponse response = client.getItem(request);
        return response.hasItem();
    }

    private Chat toChat(Map<String, AttributeValue> item) {
        return new Chat(
            item.get("chatId").s(),
            Long.parseLong(item.get("conversationId").s()),
            item.get("sender").s(),
            item.get("content").s(),
            Instant.parse(item.get("sentAt").s()),
            ChatStatus.valueOf(item.get("status").s())
        );
    }

}