package com.pascs.controller;

import com.pascs.model.Document;
import com.pascs.payload.response.MessageResponse;
import com.pascs.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("documentType") String documentType) {
        try {
            Document document = fileStorageService.storeFile(file, applicationId, documentType);
            return ResponseEntity.ok(new MessageResponse("Document uploaded successfully: " + document.getFileName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = fileStorageService.getDocument(documentId);
            Resource resource = fileStorageService.loadFileAsResource(document.getFilePath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Document>> getApplicationDocuments(@PathVariable Long applicationId) {
        List<Document> documents = fileStorageService.getDocumentsByApplication(applicationId);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            fileStorageService.deleteDocument(documentId);
            return ResponseEntity.ok(new MessageResponse("Document deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/preview/{documentId}")
    public ResponseEntity<Resource> previewDocument(@PathVariable Long documentId) {
        try {
            Document document = fileStorageService.getDocument(documentId);
            Resource resource = fileStorageService.loadFileAsResource(document.getFilePath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "inline; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}