package dev.sorokin.eventmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank(message = "Login cannot be blank")
    String login;

    @NotBlank(message = "Password cannot be blank")
    String password;
}
