package com.crm.leadscontacts.lead.dto;

import com.crm.leadscontacts.shared.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LeadUpdateDTO", description = "Payload utilisé pour mettre à jour un lead")
public class LeadUpdateDTO {

    @Schema(description = "Prénom du lead", example = "Aymen")
    private String firstName;

    @Schema(description = "Nom du lead", example = "Abid")
    private String lastName;

    @Schema(description = "Numéro de téléphone", example = "+21655123456")
    private String phone;

    @Schema(description = "Entreprise", example = "Techify")
    private String company;

    @Schema(description = "Poste", example = "Engineer")
    private String jobTitle;

    @Schema(description = "Adresse du lead")
    private Address address;

    @Schema(description = "Notes internes")
    private String notes;
}
