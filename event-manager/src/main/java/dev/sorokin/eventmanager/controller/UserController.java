package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.response.UserResponse;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toResponse(user));
    }
}
