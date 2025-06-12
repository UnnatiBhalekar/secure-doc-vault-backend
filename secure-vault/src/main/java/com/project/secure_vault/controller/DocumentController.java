package com.project.secure_vault.controller;

import com.project.secure_vault.model.DocumentMetaData;
import com.project.secure_vault.repository.DocumentMetaDataRepository;
import com.project.secure_vault.service.DocumentProcessingServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final S3Client s3Client;
    private final DocumentMetaDataRepository metadataRepo;
    private final DocumentProcessingServer processingServer;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public DocumentController(S3Client s3Client,
                              DocumentMetaDataRepository metadataRepo,
                              DocumentProcessingServer processingServer) {
        this.s3Client = s3Client;
        this.metadataRepo = metadataRepo;
        this.processingServer = processingServer;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String docId = UUID.randomUUID().toString();

        // 1) upload to S3
        s3Client.putObject(b -> b.bucket(bucketName).key(docId),
                           RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 2) save metadata
        DocumentMetaData md = new DocumentMetaData();
        md.setDocId(docId);
        md.setFilename(file.getOriginalFilename());
        md.setUploadedAt(Instant.now());
        md.setStatus("Uploaded");
        metadataRepo.save(md);

        return ResponseEntity.ok(docId);
    }

    @GetMapping
    public List<DocumentMetaData> list() {
        return metadataRepo.findAll();
    }

    @GetMapping("/{docId}")
    public ResponseEntity<DocumentMetaData> detail(@PathVariable String docId) {
        DocumentMetaData md = metadataRepo.findById(docId);
        return md != null
             ? ResponseEntity.ok(md)
             : ResponseEntity.notFound().build();
    }

    /**
     * Kick off redaction.  returns 202 Accepted.
     */
    @PostMapping("/{docId}/process")
    public ResponseEntity<Void> process(@PathVariable String docId) throws IOException {
        processingServer.redact(docId);
        return ResponseEntity.accepted().build();
    }

    /**
     * Download the already‐redacted file from S3.
     */
    @GetMapping("/{docId}/download-redacted")
    public ResponseEntity<byte[]> downloadRedacted(@PathVariable String docId) {
        String key = "redacted-" + docId + ".txt";
        try {
            byte[] data = s3Client.getObject(b -> b.bucket(bucketName).key(key))
                                  .readAllBytes();
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + key + "\"")
                .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
