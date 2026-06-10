package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;
}
