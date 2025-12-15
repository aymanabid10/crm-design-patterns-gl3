package com.crm.leadscontacts.contact.dto;

import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.shared.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ContactDTO", description = "Représentation complète d'un contact")
public class ContactDTO {

    @Schema(description = "Identifiant unique du contact", example = "42")
    private Long id;

    @Schema(description = "Prénom du contact", example = "Aymen")
    private String firstName;

    @Schema(description = "Nom du contact", example = "Abid")
    private String lastName;

    @Schema(description = "Adresse email du contact", example = "aymen.abid@example.com")
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+21655667788")
    private String phone;

    @Schema(description = "Nom de l'entreprise", example = "Techify")
    private String company;

    @Schema(description = "Poste du contact", example = "Software Engineer")
    private String jobTitle;

    @Schema(description = "Type du contact", example = "LEAD")
    private ContactType type;

    @Schema(description = "Adresse du contact")
    private Address address;

    @Schema(description = "Notes internes", example = "Client très intéressé par une démo.")
    private String notes;

    @Schema(description = "Utilisateur assigné", example = "mohamed.yassine")
    private String assignedTo;

    @Schema(description = "Dernière interaction enregistrée", example = "2025-01-12T14:22:00")
    private LocalDateTime lastInteractionDate;

    @Schema(description = "Valeur vie estimée du client", example = "12000.5")
    private Double lifetimeValue;

    @Schema(description = "Date de création", example = "2024-12-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date de la dernière mise à jour", example = "2024-12-10T18:30:00")
    private LocalDateTime updatedAt;
}
