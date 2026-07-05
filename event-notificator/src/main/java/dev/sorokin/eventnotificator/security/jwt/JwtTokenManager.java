package dev.sorokin.eventnotificator.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtTokenManager {

    private final SecretKey key;

    public JwtTokenManager(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
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