package dev.sorokin.eventmanager.dto.response;

public record LocationResponse(
        Long id,
        String name,
        String address,
        Integer age,
        String description

) {
}
