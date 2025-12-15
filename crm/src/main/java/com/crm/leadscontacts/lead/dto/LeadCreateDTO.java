package com.crm.leadscontacts.lead.dto;

import com.crm.leadscontacts.lead.domain.LeadSource;
import com.crm.leadscontacts.shared.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LeadCreateDTO", description = "Payload utilisé pour créer un lead")
public class LeadCreateDTO {

    @Schema(description = "Prénom du lead", example = "Aymen")
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @Schema(description = "Nom du lead", example = "Abid")
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Schema(description = "Adresse email du lead", example = "aymen.abid@example.com")
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+21655443322")
    private String phone;

    @Schema(description = "Entreprise du lead", example = "Techify")
    private String company;

    @Schema(description = "Poste du lead", example = "Product Manager")
    private String jobTitle;

    @Schema(description = "Source du lead", example = "WEBSITE")
    private LeadSource source;

    @Schema(description = "Adresse du lead")
    private Address address;

    @Schema(description = "Notes internes sur le lead", example = "Très intéressé par un appel découverte.")
    private String notes;

    @Schema(description = "Utilisateur assigné", example = "aymen.abid")
    @NotBlank(message = "L'utilisateur assigné est obligatoire")
    private String assignedTo;
}
