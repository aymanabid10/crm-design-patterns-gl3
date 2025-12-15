package com.crm.leadscontacts.shared.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Classe abstraite de base pour toutes les entités du CRM
 * Centralise les métadonnées communes et unifie le cycle de vie des entités
 *
 * Design Pattern: Template Method - définit la structure commune des entités
 * Principe SOLID: SRP - responsabilité unique de gestion des métadonnées
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private Long version;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Méthode pour l'archivage logique (soft delete)
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Méthode pour la réactivation
     */
    public void activate() {
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
