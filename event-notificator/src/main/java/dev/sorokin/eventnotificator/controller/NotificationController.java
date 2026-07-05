package dev.sorokin.eventnotificator.controller;

import dev.sorokin.eventnotificator.domain.Notification;
import dev.sorokin.eventnotificator.dto.request.MarkNotificationsAsReadRequest;
import dev.sorokin.eventnotificator.dto.response.NotificationResponse;
import dev.sorokin.eventnotificator.mapper.NotificationMapper;
import dev.sorokin.eventnotificator.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        List<Notification> notifications = notificationService.getUnreadNotifications();

        return ResponseEntity.status(HttpStatus.OK).body(notificationMapper.toResponseList(notifications));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Void> markNotificationAsRead(@Valid @RequestBody MarkNotificationsAsReadRequest request) {
        notificationService.markAsRead(notificationMapper.toNotificationIdList(request));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
