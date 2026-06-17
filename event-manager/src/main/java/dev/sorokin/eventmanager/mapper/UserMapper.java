package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.request.SignInRequest;
import dev.sorokin.eventmanager.dto.request.SignUpRequest;
import dev.sorokin.eventmanager.dto.response.UserResponse;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDtoFromSignUpRequest(SignUpRequest request) {

        return new UserDto(
                request.getLogin(),
                request.getPassword(),
                request.getAge()
        );
    }

    public UserDto toDtoFromSignInRequest(SignInRequest request) {
        return new UserDto(
                request.getLogin(),
                request.getPassword(),
                null
        );
    }

    public User toDomainFromEntity(UserAccountEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                entity.getPasswordHash(),
                entity.getRole()
        );
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.login(),
                user.age(),
                user.role().name()
        );
    }

    public UserAccountEntity toEntity(UserDto dto, String passwordHash) {
        UserAccountEntity entity = new UserAccountEntity();

        entity.setLogin(dto.login());
        entity.setAge(dto.age());
        entity.setPasswordHash(passwordHash);
        entity.setRole(UserRole.USER);

        return entity;
    }
}