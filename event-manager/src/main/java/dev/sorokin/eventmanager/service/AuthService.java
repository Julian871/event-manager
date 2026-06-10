package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.dto.UserDto;

public interface AuthService {

    User signUp(UserDto dto);

    String signIn(UserDto dto);
}
