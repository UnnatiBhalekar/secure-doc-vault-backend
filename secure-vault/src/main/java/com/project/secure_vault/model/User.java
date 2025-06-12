package com.project.secure_vault.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class User {
  private String username;
  private String passwordHash;

  @DynamoDbPartitionKey
  public String getUsername() { return username; }
  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
}
