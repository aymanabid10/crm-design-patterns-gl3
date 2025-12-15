package com.crm.leadscontacts.contact.domain;

import com.crm.leadscontacts.shared.domain.Address;
import com.crm.leadscontacts.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité Contact - représente un client ou prospect converti
 *
 * Principe SOLID: SRP - gère uniquement les données d'un contact
 */
@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {
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
    private ContactType type;

    @Embedded
    private Address address;

    @Column(length = 2000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime lastInteractionDate;

    @Column(nullable = false)
    private String assignedTo;

    private Double lifetimeValue;

    @PrePersist
    protected void onCreate() {
        if (type == null) {
            type = ContactType.LEAD;
        }
        if (lastInteractionDate == null) {
            lastInteractionDate = LocalDateTime.now();
        }
        if (lifetimeValue == null) {
            lifetimeValue = 0.0;
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateLastInteraction() {
        this.lastInteractionDate = LocalDateTime.now();
    }
}
