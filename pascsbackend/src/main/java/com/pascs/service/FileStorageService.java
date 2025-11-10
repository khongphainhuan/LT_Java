package com.pascs.service;

import com.pascs.model.Document;
import com.pascs.repository.DocumentRepository;
import com.pascs.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads")
                .toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Document storeFile(MultipartFile file, Long applicationId, String documentType) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            
            String fileExtension = "";
            if (fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf("."));
            }
            
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            Document document = new Document();
            document.setFileName(fileName);
            document.setFilePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setFileType(file.getContentType());
            document.setDocumentType(documentType);
            document.setApplicationId(applicationId);
            
            return documentRepository.save(document);
            
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + fileName);
        }
    }

    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    }

    public List<Document> getDocumentsByApplication(Long applicationId) {
        return documentRepository.findByApplicationId(applicationId);
    }

    public void deleteDocument(Long documentId) {
        Document document = getDocument(documentId);
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
            documentRepository.delete(document);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }

    public String getFileStorageLocation() {
        return this.fileStorageLocation.toString();
    }
}