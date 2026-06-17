package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.security.jwt.JwtTokenManager;
import dev.sorokin.eventmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenManager jwtTokenManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signUp(UserDto dto) {
        if(userRepository.existsByLogin(dto.login())) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }

        String passwordHash = passwordEncoder.encode(dto.password());

        UserAccountEntity entity = userMapper.toEntity(dto, passwordHash);
        userRepository.save(entity);

        return userMapper.toDomainFromEntity(entity);
    }

    @Override
    public String signIn(UserDto dto) {
        UserAccountEntity entity = userRepository.findByLogin(dto.login()).orElseThrow(
                () -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED)
        );

        if(!passwordEncoder.matches(dto.password(), entity.getPasswordHash())) {
            throw new ApiException("Incorrect email or password", HttpStatus.UNAUTHORIZED);
        }

        return jwtTokenManager.generateToken(userMapper.toDomainFromEntity(entity));
    }
}
