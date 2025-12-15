package com.crm.leadscontacts.lead.domain;

/**
 * Sources d'acquisition des leads
 */
public enum LeadSource {
    WEBSITE("Site Web"),
    REFERRAL("Référence"),
    SOCIAL_MEDIA("Réseaux Sociaux"),
    EMAIL_CAMPAIGN("Campagne Email"),
    PHONE_CALL("Appel Téléphonique"),
    TRADE_SHOW("Salon Professionnel"),
    PARTNER("Partenaire"),
    OTHER("Autre");

    private final String displayName;

    LeadSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
