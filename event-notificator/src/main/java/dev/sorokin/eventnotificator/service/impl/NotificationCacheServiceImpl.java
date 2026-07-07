package dev.sorokin.eventnotificator.service.impl;

import dev.sorokin.eventnotificator.service.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static dev.sorokin.eventnotificator.redis.CacheConstants.CACHE_UNREAD_KEY_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheServiceImpl implements NotificationCacheService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void incrementUnreadCounter(Long userId) {

        try {
            String key = CACHE_UNREAD_KEY_PREFIX + userId;
            redisTemplate.opsForValue().increment(key);
            log.info("increment unread counter for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to increment unread counter for user {}", userId, e);
        }
    }

    @Override
    public void saveCountUnreadNotification(Long userId, Long count) {
        try {
            String key = CACHE_UNREAD_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(key, String.valueOf(count));
            log.info("save unread counter for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to save unread counter for user {}", userId, e);
        }
    }
}
