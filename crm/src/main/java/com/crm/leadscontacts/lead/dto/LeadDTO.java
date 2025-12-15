package com.crm.leadscontacts.lead.dto;

import com.crm.leadscontacts.lead.domain.LeadSource;
import com.crm.leadscontacts.lead.domain.LeadStatus;
import com.crm.leadscontacts.shared.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LeadDTO", description = "Représentation complète d'un lead")
public class LeadDTO {

    @Schema(description = "Identifiant du lead", example = "15")
    private Long id;

    @Schema(description = "Prénom du lead", example = "Aymen")
    private String firstName;

    @Schema(description = "Nom du lead", example = "Abid")
    private String lastName;

    @Schema(description = "Adresse email", example = "aymen.abid@example.com")
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+21655998877")
    private String phone;

    @Schema(description = "Entreprise", example = "Techify")
    private String company;

    @Schema(description = "Poste", example = "CTO")
    private String jobTitle;

    @Schema(description = "Statut actuel du lead", example = "QUALIFIED")
    private LeadStatus status;

    @Schema(description = "Source du lead", example = "LINKEDIN")
    private LeadSource source;

    @Schema(description = "Score attribué au lead", example = "85")
    private Integer score;

    @Schema(description = "Adresse du lead")
    private Address address;

    @Schema(description = "Notes internes")
    private String notes;

    @Schema(description = "Utilisateur assigné", example = "yassine.kallel")
    private String assignedTo;

    @Schema(description = "Date du dernier contact", example = "2025-02-01T10:00:00")
    private LocalDateTime lastContactDate;

    @Schema(description = "Date de qualification en lead qualifié", example = "2025-02-05T15:20:00")
    private LocalDateTime qualifiedDate;

    @Schema(description = "Date de conversion en contact", example = "2025-02-10T09:00:00")
    private LocalDateTime convertedDate;

    @Schema(description = "ID du contact créé lors de la conversion", example = "42")
    private Long convertedToContactId;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;
}
