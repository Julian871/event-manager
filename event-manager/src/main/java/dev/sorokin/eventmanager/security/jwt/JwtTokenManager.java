package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.domain.User;
import dev.sorokin.eventmanager.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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

    public Claims getClaimsFromToken(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (JwtException e) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }
}
