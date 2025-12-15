package com.crm.leadscontacts.shared.handlers;

/**
        * Exception pour les ressources non trouvées
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s avec l'ID %d non trouvé", resourceName, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
