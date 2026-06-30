package dev.sorokin.eventmanager.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class EventUpdateRequest {

    private String name;

    @Min(value = 0, message = "Minimum places: 0")
    private Integer maxPlaces;

    @Future(message = "Start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
    private LocalDateTime date;

    @Min(value = 1, message = "Minimum cost: 1")
    private Integer cost;

    @Min(value = 30, message = "Minimum duration: 30")
    private Integer duration;

    @Positive(message = "Incorrect location ID")
    private Long locationId;
}
