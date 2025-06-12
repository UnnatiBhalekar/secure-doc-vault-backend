# secure-doc-vault-backend
Spring Boot microservice for Secure Document Vault

A simple secure document vault application. Users can sign up, log in, upload PDFs to S3, view metadata from DynamoDB, redact PII via AWS Textract + Comprehend, and download the redacted results.

# Repository Contents
1. backend/ — Spring Boot service that exposes a JWT-secured REST API on port 8080:

- /api/auth — signup & login → returns JWT

- /api/documents — list, detail, upload, process, download-redacted

- DynamoDB for metadata, S3 for PDFs, Textract + Comprehend for PII redaction

2. frontend/ — Angular 20 SPA served on port 4200:

# Login & signup forms

1. Dashboard list of documents

2. Upload page (with progress bar + snackbars)

3. Detail page (redact / download buttons)

4. JWT stored in LocalStorage + Angular HTTP interceptor

# Backend Setup
1. Prerequisites
- Java 17+

- Maven 3.8+

- AWS credentials configured in ~/.aws/credentials (or environment variables)

- AWS resources:

- DynamoDB table (primary key username for users, docId for metadata)

- S3 bucket for uploads (name in application.yml)

- (optional) Textract & Comprehend APIs enabled in your account

# Configuration
Create src/main/resources/application.yml:

<img width="572" alt="Screenshot 2025-06-12 at 4 22 09 AM" src="https://github.com/user-attachments/assets/2fd47e2c-6b2d-4297-86f1-aa2c78a0d462" />

Build & Run
bash
Copy
cd backend
mvn clean package
java -jar target/secure-vault-0.0.1-SNAPSHOT.jar
The API will be available at http://localhost:8080.

# Endpoints
1. POST	/api/auth/signup	
> Register new user, returns JWT
2. POST	/api/auth/login	
> Log in, returns JWT
3. GET	/api/documents
> List all document metadata
4. GET	/api/documents/{docId}
> Get single document metadata
5. POST	/api/documents/upload	
> Upload file (multipart) → returns docId
6. POST	/api/documents/{docId}/process	
> Asynchronously redact via Textract/PII
7. GET	/api/documents/{docId}/download-redacted
> Download redacted .txt file

* All document endpoints are currently open to simplify testing; in production you’d lock down everything except your own user’s resources.



# To Do / Future Improvements
1. Lock down document endpoints so only the owner can view their own docs

2. Switch to Lambda DSL in Spring Security (no more deprecated APIs)

3. Add pagination & sorting on the dashboard

4. Show live “processing” status (e.g. websockets or periodic polling)

5. Add file-type previews (PDF viewer)

6. Implement upload cancellation & retry

Feel free to fork, raise issues or send PR’s!
— Unnati Bhalekar / SecureDocVault






