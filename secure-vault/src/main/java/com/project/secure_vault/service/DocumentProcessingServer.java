package com.project.secure_vault.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.secure_vault.repository.DocumentMetaDataRepository;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectPiiEntitiesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Document;

@Service
public class DocumentProcessingServer {
    private final S3Client s3;
  private final TextractClient textract;
  private final ComprehendClient comprehend;
  private final DocumentMetaDataRepository metaRepo;

  @Value("${aws.s3.bucket}")
  private String bucket;

  public DocumentProcessingServer(
      S3Client s3,
      TextractClient textract,
      ComprehendClient comprehend,
      DocumentMetaDataRepository metaRepo) {
    this.s3 = s3;
    this.textract = textract;
    this.comprehend = comprehend;
    this.metaRepo = metaRepo;
  }

  public void redact(String docId) throws IOException {
    // 1) download
    byte[] data;
    try {
      data = s3.getObject(b -> b.bucket(bucket).key(docId))
               .readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read S3 object bytes", e);
    }
    // 2) extract text
    String text = textract.detectDocumentText(r -> r.document(
        Document.builder().bytes(SdkBytes.fromByteArray(data)).build()))
      .blocks().stream()
      .filter(b -> b.blockType()==BlockType.LINE)
      .map(Block::text)
      .collect(Collectors.joining("\n"));
    // 3) detect PII
    DetectPiiEntitiesResponse pii = comprehend.detectPiiEntities(r -> 
        r.text(text).languageCode("en"));
    // 4) apply redactions
    StringBuilder redacted = new StringBuilder(text);
    int delta = 0;
    for (var e : pii.entities()) {
      int start = e.beginOffset()+delta;
      int end   = e.endOffset()+delta;
      int len   = end-start;
      redacted.replace(start, end, "*".repeat(len));
      delta += "*".repeat(len).length() - len;
    }
    // 5) upload redacted text
    String key = "redacted-" + docId + ".txt";
    s3.putObject(b -> b.bucket(bucket).key(key),
                 RequestBody.fromString(redacted.toString()));
    // 6) update metadata
    var md = metaRepo.findById(docId);
    md.setStatus("Redacted");
    metaRepo.save(md);
  }
}
