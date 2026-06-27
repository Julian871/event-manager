package dev.sorokin.eventmanager.util;

import dev.sorokin.eventmanager.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public String getCurrentUserLogin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null)
            throw new ApiException("Authentication not present", HttpStatus.UNAUTHORIZED);

        return (String) auth.getPrincipal();
    }

    public String getCurrentUserRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null)
            throw new ApiException("Authentication not present", HttpStatus.UNAUTHORIZED);

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new ApiException("Role not found", HttpStatus.UNAUTHORIZED));
    }
}
