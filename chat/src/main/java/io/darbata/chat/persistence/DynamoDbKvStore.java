package io.darbata.chat.persistence;

import io.darbata.chat.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.*;

@Repository
public class DynamoDbKvStore implements KVStore {

    @Value("${dynamodb.table-name}")
    private String tableName;

    private final DynamoDbClient client;
    private final Logger logger = LoggerFactory.getLogger(DynamoDbKvStore.class);

    public DynamoDbKvStore(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void put(String partitionKey, String sortKey, Message message) {

        // create list of items
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("conversationId", AttributeValue.builder().s(partitionKey).build());
        itemValues.put("messageId", AttributeValue.builder().s(message.getId().toString()).build());
        itemValues.put("content", AttributeValue.builder().s(message.getContent()).build());
        itemValues.put("from", AttributeValue.builder().s(Long.toString(message.getFrom())).build());
        itemValues.put("to", AttributeValue.builder().s(Long.toString(message.getTo())).build());
        itemValues.put("createdAt", AttributeValue.builder().s(message.getCreatedAt().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            client.putItem(request);
            logger.info("Stored message: {}", message);
        } catch (Exception e) {
            logger.error("Failed to store message", e);
        }

    }

    @Override
    public List<Message> get(String partitionKey) {

        Map<String, AttributeValue> keys = Map.of(
                "conversationId", AttributeValue.builder().s(partitionKey).build());

        Map<String, KeysAndAttributes> requestItems = new HashMap<>();
        requestItems.put(tableName, KeysAndAttributes.builder().keys(List.of(keys)).build());

        BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(requestItems)
                .build();

        List<Message> messages = new ArrayList<>();

        client.batchGetItemPaginator(request).stream()
                .flatMap(response -> response.responses().getOrDefault(tableName, Collections.emptyList()).stream())
                .forEach(item -> {
                    messages.add((Message) item);
                });

        return messages;
    }
}