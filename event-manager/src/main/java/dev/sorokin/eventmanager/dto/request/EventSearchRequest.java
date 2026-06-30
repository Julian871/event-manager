package dev.sorokin.eventmanager.dto.request;

import dev.sorokin.eventmanager.enums.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EventSearchRequest {
    private String name;

    @Min(value = 0, message = "Minimum places: 0")
    private Integer minPlaces;

    @Min(value = 0, message = "Minimum places: 0")
    private Integer maxPlaces;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?$",
            message = "dateStartAfter must be in format 'YYYY-MM-DDThh:mm:ss'"
    )
    private String dateStartAfter;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?$",
            message = "dateStartBefore must be in format 'YYYY-MM-DDThh:mm:ss'"
    )
    private String dateStartBefore;

    @Min(value = 1, message = "Minimum cost: 1")
    private Integer costMin;

    @Min(value = 1, message = "Minimum cost: 1")
    private Integer costMax;

    @Min(value = 30, message = "Minimum duration: 30")
    private Integer durationMin;

    @Min(value = 30, message = "Minimum duration: 30")
    private Integer durationMax;

    @Positive(message = "Incorrect location ID")
    private Long locationId;

    private EventStatus eventStatus;
}
