package io.darbata.dispatcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.sdk.dynamo-db.url}")
    private String url;

    @Value("${aws.sdk.dynamo-db.table}")
    private String table;

    @Bean
    public DynamoDbClient dynamodDbClient() {
        DynamoDbClient ddb = DynamoDbClient
                .builder()
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(URI.create(url))
                .build();

        initTables(ddb);
        return ddb;
    }

    private void initTables(DynamoDbClient ddb) {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(table)
                .attributeDefinitions(
                        AttributeDefinition.builder()
                            .attributeName("conversationId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                        AttributeDefinition.builder()
                                .attributeName("chatId")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName("conversationId")
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName("chatId")
                                .keyType(KeyType.RANGE)
                                .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        try {
            ddb.createTable(createTableRequest);
        } catch (Exception ignored) {
            // table exists
            return;
        }
    }

}