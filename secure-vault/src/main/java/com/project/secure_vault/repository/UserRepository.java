package com.project.secure_vault.repository;

import com.project.secure_vault.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;

@Repository
public class UserRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.users-table}")
    private String usersTable;

    // Spring will call this constructor and inject enhancedClient
    public UserRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<User> table() {
        return enhancedClient.table(usersTable, TableSchema.fromBean(User.class));
    }

    public void save(User user) {
        table().putItem(user);
    }

    public User findByUsername(String username) {
        return table().getItem(r -> r.key(k -> k.partitionValue(username)));
    }
}
