package com.crm.leadscontacts.lead.domain;

/**
 * Énumération des statuts possibles d'un Lead
 */
public enum LeadStatus {
    NEW("Nouveau"),
    CONTACTED("Contacté"),
    QUALIFIED("Qualifié"),
    UNQUALIFIED("Non Qualifié"),
    CONVERTED("Converti");

    private final String displayName;

    LeadStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

