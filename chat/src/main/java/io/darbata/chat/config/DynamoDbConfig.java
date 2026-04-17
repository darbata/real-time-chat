package io.darbata.chat.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.sdk.dynamo-db.table}")
    private String tableName;

    @Value("${aws.sdk.dynamo-db.url}")
    private String dbUrl;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClient ddb = DynamoDbClient
                .builder()
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(URI.create(dbUrl))
                .build();
        initTables(ddb); // todo: remove table definition from application code on deploment
        return ddb;
    }

    // define table for local development
    private void initTables(DynamoDbClient ddb) {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions( // using composite keys
                        AttributeDefinition.builder()
                                .attributeName("conversationId") // primary
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("messageId") // sort
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName("conversationId")
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName("messageId")
                                .keyType(KeyType.RANGE)
                                .build())
                .billingMode(BillingMode.PAY_PER_REQUEST) // TODO: implement provisioned capacity in deployment
                .build();
        try {
            ddb.createTable(createTableRequest);
        } catch (Exception ignored) {
            // table exists already
            return;
        }
    }
}