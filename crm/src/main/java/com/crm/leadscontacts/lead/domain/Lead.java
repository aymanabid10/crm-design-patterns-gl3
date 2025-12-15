package com.crm.leadscontacts.lead.domain;

import com.crm.leadscontacts.shared.domain.Address;
import com.crm.leadscontacts.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité Lead - représente un prospect potentiel
 *
 * User Story: En tant que Responsable Commercial, je peux créer et qualifier un lead
 * Principe SOLID: SRP - gère uniquement les données d'un lead
 */
@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String company;

    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status;

    @Enumerated(EnumType.STRING)
    private LeadSource source;

    @Builder.Default
    @Column(nullable = false)
    private Integer score = 0;

    @Embedded
    private Address address;

    @Column(length = 2000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime lastContactDate;

    private LocalDateTime qualifiedDate;

    private LocalDateTime convertedDate;

    private Long convertedToContactId;

    @Column(nullable = false)
    private String assignedTo;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = LeadStatus.NEW;
        }
        if (lastContactDate == null) {
            lastContactDate = LocalDateTime.now();
        }
    }

    /**
     * Qualifier un lead
     * User Story: ajouter un score et un statut afin de prioriser le suivi
     */
    public void qualify(Integer newScore) {
        this.score = newScore;
        this.status = LeadStatus.QUALIFIED;
        this.qualifiedDate = LocalDateTime.now();
    }

    /**
     * Marquer le lead comme non qualifié
     */
    public void disqualify(String reason) {
        this.status = LeadStatus.UNQUALIFIED;
        this.notes = (this.notes != null ? this.notes + "\\n" : "") +
                "Disqualifié: " + reason;
    }

    /**
     * Marquer le lead comme contacté
     */
    public void markAsContacted() {
        this.status = LeadStatus.CONTACTED;
        this.lastContactDate = LocalDateTime.now();
    }

    /**
     * Convertir le lead en contact
     * User Story: je peux convertir un lead en contact afin de le faire évoluer
     */
    public void convertToContact(Long contactId) {
        this.status = LeadStatus.CONVERTED;
        this.convertedDate = LocalDateTime.now();
        this.convertedToContactId = contactId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
