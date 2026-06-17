package dev.sorokin.eventmanager.dto;

public record UserDto(
        String login,
        String password,
        Integer age
) {
}
