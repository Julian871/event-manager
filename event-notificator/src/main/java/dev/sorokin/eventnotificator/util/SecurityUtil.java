package dev.sorokin.eventnotificator.util;

import dev.sorokin.eventnotificator.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null)
            throw new ApiException("Authentication not present", HttpStatus.UNAUTHORIZED);

        return (Long) auth.getPrincipal();
    }
}
