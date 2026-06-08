package dev.sorokin.eventmanager.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotNull(message = "Capacity cannot be blank")
    @Min(value = 5, message = "Minimum capacity: 5")
    private Integer capacity;

    private String description;
}
