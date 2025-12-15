package com.crm.leadscontacts.shared.notification;

/**
 * Interface pour le système de notifications
 *
 * Design Pattern: Observer Pattern
 * Principe SOLID: DIP - abstraction pour les notifications
 * Principe SOLID: ISP - interface focalisée sur les notifications
 */
public interface INotifier {

    /**
     * Envoie une notification à un utilisateur
     */
    void sendNotification(String userId, String message, NotificationType type);

    /**
     * Envoie une alerte système
     */
    void sendAlert(String message, AlertPriority priority);

    /**
     * Notifie plusieurs utilisateurs
     */
    void notifyUsers(java.util.List<String> userIds, String message, NotificationType type);
}
