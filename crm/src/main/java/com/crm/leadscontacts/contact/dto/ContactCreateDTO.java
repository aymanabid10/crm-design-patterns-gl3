package com.crm.leadscontacts.contact.dto;

import com.crm.leadscontacts.contact.domain.ContactType;
import com.crm.leadscontacts.shared.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ContactCreateDTO", description = "Payload utilisé pour créer un nouveau contact")
public class ContactCreateDTO {

    @Schema(description = "Prénom du contact", example = "Aymen", required = true)
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @Schema(description = "Nom du contact", example = "Abid", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Schema(description = "Adresse email du contact", example = "aymen.abid@example.com", required = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @Schema(description = "Numéro de téléphone du contact", example = "+21655998877")
    private String phone;

    @Schema(description = "Entreprise du contact", example = "Techify")
    private String company;

    @Schema(description = "Poste du contact", example = "Software Engineer")
    private String jobTitle;

    @Schema(description = "Type de contact", example = "LEAD", required = true)
    @NotNull(message = "Le type de contact est obligatoire")
    private ContactType type;

    @Schema(description = "Adresse du contact")
    private Address address;

    @Schema(description = "Notes internes concernant le contact", example = "Client intéressé par une démo produit")
    private String notes;

    @Schema(description = "Utilisateur assigné à ce contact", example = "mohamed.yassine", required = true)
    @NotBlank(message = "L'utilisateur assigné est obligatoire")
    private String assignedTo;
}
