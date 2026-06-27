package dev.sorokin.eventmanager.dto.response;

import java.time.LocalDateTime;

public record EventResponse (
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId,
        String status
) {
}
