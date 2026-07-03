package dev.sorokin.eventnotificator.service;

import dev.sorokin.eventnotificator.domain.Notification;
import dev.sorokin.kafka.EventChangeMessage;

import java.util.List;

public interface NotificationService {

    void processEventChange(EventChangeMessage message);

    List<Notification> getUnreadNotifications();

    void markAsRead(List<Long> notificationIds);
}
