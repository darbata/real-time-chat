package io.darbata.dispatcher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.time.Duration;

@Configuration
public class DynamoDbConfig {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbConfig.class);

    @Value("${aws.sdk.dynamo-db.url}")
    private String url;

    @Value("${aws.sdk.dynamo-db.table}")
    private String table;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(URI.create(url))
                // DynamoDB Local accepts any credentials; setting them explicitly
                // stops the default chain from probing the (nonexistent) IMDS endpoint.
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("local", "local")))
                .overrideConfiguration(c -> c
                        .apiCallTimeout(Duration.ofSeconds(30))
                        .apiCallAttemptTimeout(Duration.ofSeconds(30)))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initTables() {
        DynamoDbClient ddb = dynamoDbClient(); // or inject the bean; see note below
        CreateTableRequest request = CreateTableRequest.builder()
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
            ddb.createTable(request);
            log.info("Created DynamoDB table {}", table);
        } catch (ResourceInUseException e) {
            log.info("DynamoDB table {} already exists", table); // expected on every boot after the first
        } catch (SdkException e) {
            log.warn("Could not create table {}; continuing", table, e);
        }
    }
}