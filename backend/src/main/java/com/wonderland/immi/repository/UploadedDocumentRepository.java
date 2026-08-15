package com.wonderland.immi.repository;

import com.wonderland.immi.entity.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedDocumentRepository
        extends JpaRepository<UploadedDocument, Long> {

    List<UploadedDocument> findByApplicationId(Long applicationId);

}