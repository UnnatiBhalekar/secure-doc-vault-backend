package com.project.secure_vault.repository;

import com.project.secure_vault.model.DocumentMetaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import java.util.List;

@Repository
public class DocumentMetaDataRepository {
    private final DynamoDbTable<DocumentMetaData> table;

    public DocumentMetaDataRepository(DynamoDbEnhancedClient enhancedClient,
                                      @Value("${aws.dynamodb.documentmetadata-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(DocumentMetaData.class));
    }

    public void save(DocumentMetaData metadata) {
        table.putItem(metadata);
    }

    public List<DocumentMetaData> findAll() {
        return table.scan(ScanEnhancedRequest.builder().build())
                    .items()
                    .stream().toList();
    }

    public DocumentMetaData findById(String docId) {
        return table.getItem(r -> r.key(k -> k.partitionValue(docId)));
    }
}