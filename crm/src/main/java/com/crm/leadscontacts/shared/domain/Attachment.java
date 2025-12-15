package com.crm.leadscontacts.shared.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Value Object pour les pièces jointes
 * Utilisé pour stocker des fichiers liés aux entités
 *
 * Design Pattern: Value Object
 */
@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String storagePath;

    private String description;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
