package dev.sorokin.eventnotificator.service;

public interface NotificationCacheService {

    void incrementUnreadCounter(Long userId);

    void saveCountUnreadNotification(Long userId, Long count);
}
