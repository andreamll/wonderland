package com.wonderland.immi.service;

import com.wonderland.immi.entity.ImmigrationApplication;
import com.wonderland.immi.entity.UploadedDocument;
import com.wonderland.immi.repository.ImmigrationApplicationRepository;
import com.wonderland.immi.repository.UploadedDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ImmigrationApplicationService {

    private final ImmigrationApplicationRepository applicationRepository;
    private final UploadedDocumentRepository documentRepository;
    private final CloudStorageService cloudStorageService;
    private final Random random = new Random();

    public ImmigrationApplicationService(
            ImmigrationApplicationRepository applicationRepository,
            UploadedDocumentRepository documentRepository,
            CloudStorageService cloudStorageService
    ) {
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.cloudStorageService = cloudStorageService;
    }

    public ImmigrationApplication saveApplication(
            String fullName,
            String dob,
            String nationality,
            String passport,
            String visaType,
            List<String> documentTypes,
            List<MultipartFile> documents
    ) throws IOException {

        ImmigrationApplication application = new ImmigrationApplication();
        application.setFullName(fullName);
        application.setDob(dob);
        application.setNationality(nationality);
        application.setPassport(passport);
        application.setVisaType(visaType);
        application.setCreatedAt(LocalDateTime.now());

        boolean approved = random.nextBoolean();

        if (approved) {
            application.setDecision("APPROVED");
            application.setDecisionMessage(getRandomApprovedMessage());
        } else {
            application.setDecision("REJECTED");
            application.setDecisionMessage(getRandomRejectedMessage());
        }

        ImmigrationApplication savedApplication = applicationRepository.save(application);

        /// REMOVE COMMENT AFTER MIGRATING TO AWS
        // if (documents != null && documentTypes != null) {
        //     for (int i = 0; i < documents.size(); i++) {
        //         MultipartFile file = documents.get(i);

        //         if (file == null || file.isEmpty()) {
        //             continue;
        //         }

        //         String documentType = documentTypes.size() > i
        //                 ? documentTypes.get(i)
        //                 : "Unknown Document";

        //         String objectName = cloudStorageService.uploadFile(
        //                 file,
        //                 savedApplication.getId(),
        //                 documentType
        //         );

        //         UploadedDocument uploadedDocument = new UploadedDocument();
        //         uploadedDocument.setApplicationId(savedApplication.getId());
        //         uploadedDocument.setDocumentType(documentType);
        //         uploadedDocument.setOriginalFileName(file.getOriginalFilename());
        //         uploadedDocument.setBucketObjectName(objectName);
        //         uploadedDocument.setBucketUrl(cloudStorageService.buildGsUrl(objectName));
        //         uploadedDocument.setUploadedAt(LocalDateTime.now());

        //        documentRepository.save(uploadedDocument);  
        //     }
        // }

        return savedApplication;
    }

    public ImmigrationApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found."));
    }

    private String getRandomApprovedMessage() {
        List<String> messages = List.of(
                "Approved. The Mad Hatter stamped your documents during tea time.",
                "Approved. Cheshire grinned, vanished, and your visa appeared most mysteriously.",
                "Approved. The White Rabbit confirms that you are, for once, exactly on time.",
                "Approved. Wonderland welcomes you through official channels instead of rabbit holes.",
                "Approved. The Ministry of Curious Arrivals found your paperwork delightfully acceptable."
        );

        return messages.get(random.nextInt(messages.size()));
    }

    private String getRandomRejectedMessage() {
        List<String> messages = List.of(
                "Rejected. The Queen of Hearts has ordered that the paperwork be redone immediately.",
                "Rejected. Off with the application... at least for now.",
                "Rejected. The border roses were painted red, but your documents were still not convincing.",
                "Rejected. Cheshire vanished before the visa officer could finish reading your explanation letter.",
                "Rejected. The Ministry suspects unauthorized rabbit-hole travel."
        );

        return messages.get(random.nextInt(messages.size()));
    }
}