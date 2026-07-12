package dev.sorokin.eventnotificator.service;

public interface NotificationCacheService {

    void incrementUnreadCounter(Long userId, Long delta);

    void saveCountUnreadNotification(Long userId, Long count);
}
