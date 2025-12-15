package com.crm.leadscontacts.shared.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service de notifications
 *
 * Design Pattern: Observer Pattern implementation
 * Principe SOLID: SRP - responsabilité unique de gérer les notifications
 */
@Slf4j
@Service
public class NotificationService implements INotifier {

    @Override
    public void sendNotification(String userId, String message, NotificationType type) {
        log.info("[NOTIFICATION] Type: {}, User: {}, Message: {}, Time: {}",
                type, userId, message, LocalDateTime.now());
        // Implémentation réelle avec email, SMS, push notifications, etc.
    }

    @Override
    public void sendAlert(String message, AlertPriority priority) {
        log.warn("[ALERT] Priority: {}, Message: {}, Time: {}",
                priority, message, LocalDateTime.now());
        // Implémentation réelle pour les alertes système
    }

    @Override
    public void notifyUsers(List<String> userIds, String message, NotificationType type) {
        userIds.forEach(userId -> sendNotification(userId, message, type));
    }
}
