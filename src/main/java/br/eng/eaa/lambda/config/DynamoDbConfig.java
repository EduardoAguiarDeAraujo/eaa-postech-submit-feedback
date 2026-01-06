package br.eng.eaa.lambda.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class DynamoDbConfig {

    @Produces
    @ApplicationScoped
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient standardClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(standardClient)
                .build();
    }
}
