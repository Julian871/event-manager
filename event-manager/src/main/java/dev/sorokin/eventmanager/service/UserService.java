package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.User;

public interface UserService {

    User getUserById(Long id);

    void createDefaultAdmin();
}
