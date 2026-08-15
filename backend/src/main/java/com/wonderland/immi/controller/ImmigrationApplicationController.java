package com.wonderland.immi.controller;

import com.wonderland.immi.dto.ApplicationResponse;
import com.wonderland.immi.entity.ImmigrationApplication;
import com.wonderland.immi.service.ImmigrationApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/applications")
@CrossOrigin(origins = "*")
public class ImmigrationApplicationController {

    private final ImmigrationApplicationService immigrationApplicationService;

    public ImmigrationApplicationController(ImmigrationApplicationService immigrationApplicationService) {
        this.immigrationApplicationService = immigrationApplicationService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApplicationResponse> createApplication(
            @RequestParam("fullName") String fullName,
            @RequestParam("dob") String dob,
            @RequestParam("nationality") String nationality,
            @RequestParam("passport") String passport,
            @RequestParam("visaType") String visaType,
            @RequestParam(value = "documentTypes[]", required = false) List<String> documentTypes,
            @RequestParam(value = "documents[]", required = false) List<MultipartFile> documents
    ) throws IOException {

        System.out.println("fullName = " + fullName);
        System.out.println("visaType = " + visaType);
        System.out.println("documentTypes = " + documentTypes);
        System.out.println("documents null? " + (documents == null));

        if (documents != null) {
            System.out.println("documents size = " + documents.size());
            for (MultipartFile file : documents) {
                System.out.println("file name = " + file.getOriginalFilename());
                System.out.println("file empty? " + file.isEmpty());
            }
        }

        ImmigrationApplication savedApplication = immigrationApplicationService.saveApplication(
                fullName,
                dob,
                nationality,
                passport,
                visaType,
                documentTypes,
                documents
        );

        ApplicationResponse response = new ApplicationResponse(
                savedApplication.getId(),
                savedApplication.getDecision(),
                savedApplication.getDecisionMessage()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {

        ImmigrationApplication application = immigrationApplicationService.getApplicationById(id);

        ApplicationResponse response = new ApplicationResponse(
                application.getId(),
                application.getDecision(),
                application.getDecisionMessage()
        );

        return ResponseEntity.ok(response);
    }
}