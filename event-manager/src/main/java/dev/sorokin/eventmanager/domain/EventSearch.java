package dev.sorokin.eventmanager.domain;

import dev.sorokin.eventmanager.enums.EventStatus;


public record EventSearch(
        String name,
        Integer minPlaces,
        Integer maxPlaces,
        String dateStartAfter,
        String dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus
) {
}
