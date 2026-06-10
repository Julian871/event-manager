package dev.sorokin.eventmanager.domain;

import dev.sorokin.eventmanager.enums.UserRole;

public record User(
        Long id,
        String login,
        Integer age,
        String passwordHash,
        UserRole role
) {
}
