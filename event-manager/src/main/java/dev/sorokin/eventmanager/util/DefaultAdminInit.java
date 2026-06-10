package dev.sorokin.eventmanager.util;

import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminInit {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        if(userRepository.existsByLogin("admin")) return;

        userService.createDefaultAdmin();
    }
}
