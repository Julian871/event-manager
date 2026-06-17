package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.admin.login}")
    private String defaultAdminLogin;
    @Value("${default.admin.password}")
    private String defaultAdminPassword;

    @Override
    public User getUserById(Long id) {
        UserAccountEntity entity = userRepository.findById(id).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.NOT_FOUND)
        );

        return userMapper.toDomainFromEntity(entity);
    }

    @Override
    public void createDefaultAdmin() {
        UserAccountEntity entity = new UserAccountEntity();
        String passwordHash = passwordEncoder.encode(defaultAdminPassword);
        entity.setLogin(defaultAdminLogin);
        entity.setAge(18);
        entity.setRole(UserRole.ADMIN);
        entity.setPasswordHash(passwordHash);
        userRepository.save(entity);
    }
}