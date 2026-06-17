package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenManager {

    private final SecretKey key;
    private final long expirationTime;

    public JwtTokenManager(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.lifetime}") long expirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.expirationTime = expirationTime;
    }

    public String generateToken(User user) {
        return Jwts
                .builder()
                .subject(user.login())
                .claim("userId", user.id())
                .claim("role", user.role().name())
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    public boolean isValidToken(String jwtToken) {
        try {
            Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken);

            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}