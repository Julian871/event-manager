package dev.sorokin.eventmanager.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class EventCreateRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Max places cannot be blank")
    @Min(value = 0, message = "Minimum places: 0")
    private Integer maxPlaces;

    @NotNull(message = "Start date cannot be blank")
    @Future(message = "Start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
    private LocalDateTime date;

    @NotNull(message = "Cost cannot be blank")
    @Min(value = 1, message = "Minimum cost: 1")
    private Integer cost;

    @NotNull(message = "Duration cannot be blank")
    @Min(value = 30, message = "Minimum duration: 30")
    private Integer duration;

    @NotNull(message = "Location ID cannot be blank")
    @Positive(message = "Incorrect location ID")
    private Long locationId;
}
