package org.d4rckh.limiterx.spring.extractor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UsernameExtractor implements KeyExtractor {
    public String extract() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ((principal instanceof UserDetails userDetails)) {
            return userDetails.getUsername();
        }

        return "anonymous";
    }
}