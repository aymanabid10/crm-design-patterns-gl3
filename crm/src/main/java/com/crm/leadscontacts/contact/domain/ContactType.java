package com.crm.leadscontacts.contact.domain;

public enum ContactType {
    LEAD("Prospect"),
    CUSTOMER("Client"),
    PARTNER("Partenaire"),
    VENDOR("Fournisseur");

    private final String displayName;

    ContactType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
