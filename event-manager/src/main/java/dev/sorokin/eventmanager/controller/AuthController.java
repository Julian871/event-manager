package dev.sorokin.eventmanager.controller;


import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.request.SignInRequest;
import dev.sorokin.eventmanager.dto.request.SignUpRequest;
import dev.sorokin.eventmanager.dto.response.TokenResponse;
import dev.sorokin.eventmanager.dto.response.UserResponse;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        User user = authService.signUp(userMapper.toDtoFromSignUpRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
        String token = authService.signIn(userMapper.toDtoFromSignInRequest(request));
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }
}
