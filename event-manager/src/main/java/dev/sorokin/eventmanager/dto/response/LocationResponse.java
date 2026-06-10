package dev.sorokin.eventmanager.dto.response;

public record LocationResponse(
        Long id,
        String name,
        String address,
        Integer capacity,
        String description

) {
}
