package com.crm.leadscontacts.shared.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Value Object pour les adresses
 * Partagé par tout le CRM (Contacts, Leads, etc.)
 *
 * Design Pattern: Value Object - objet immuable sans identité propre
 * Principe SOLID: SRP - représente uniquement une adresse
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s",
                street, city, state, zipCode, country);
    }
}
