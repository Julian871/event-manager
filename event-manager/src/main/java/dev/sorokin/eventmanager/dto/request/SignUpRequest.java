package dev.sorokin.eventmanager.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Login cannot be blank")
    String login;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotNull(message = "Age cannot be blank")
    @Min(value = 18, message = "Minimum age: 18")
    Integer age;
}
