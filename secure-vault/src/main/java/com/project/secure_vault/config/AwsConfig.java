package com.project.secure_vault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region:eu-west-1}")
    private String awsRegion;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                             .region(Region.of(awsRegion))
                             .credentialsProvider(DefaultCredentialsProvider.create())
                             .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                                     .dynamoDbClient(dynamoDbClient)
                                     .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                       .region(Region.of(awsRegion))
                       .credentialsProvider(DefaultCredentialsProvider.create())
                       .build();
    }

    @Bean
public TextractClient textractClient() {
  return TextractClient.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build();
}

@Bean
public ComprehendClient comprehendClient() {
  return ComprehendClient.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build();
}

}

