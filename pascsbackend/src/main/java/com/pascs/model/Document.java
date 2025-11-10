package com.pascs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private String fileType;
    private Long fileSize;
    private String documentType; // ID_CARD, HOUSEHOLD_REGISTRATION, etc.

    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    private LocalDateTime uploadedAt;
    private String description;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}