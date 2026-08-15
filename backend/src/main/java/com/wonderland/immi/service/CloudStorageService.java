package com.wonderland.immi.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CloudStorageService {

    private final Storage storage;

    @Value("${app.bucket.name}")
    private String bucketName;

    public CloudStorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file, Long applicationId, String documentType) throws IOException {

        String originalFileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replaceAll("\\s+", "_")
                : "document";

        String safeDocumentType = documentType.replaceAll("\\s+", "_");

        String objectName = "applications/" + applicationId + "/"
                + safeDocumentType + "-" + UUID.randomUUID() + "-" + originalFileName;

        System.out.println("Uploading file to bucket...");
        System.out.println("Bucket = " + bucketName);
        System.out.println("Object name = " + objectName);
        System.out.println("Content type = " + file.getContentType());

        BlobId blobId = BlobId.of(bucketName, objectName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        System.out.println("Upload completed successfully.");

        return objectName;
    }

    public String buildGsUrl(String objectName) {
        return "gs://" + bucketName + "/" + objectName;
    }
}