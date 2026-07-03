package dev.sorokin.eventnotificator.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarkNotificationsAsReadRequest {

    @NotNull(message = "Notification IDs list cannot be null")
    @NotEmpty(message = "Notification IDs list cannot be empty")
    private List<Long> notificationIds;
}
