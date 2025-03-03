package io.github.d4rckh.limiterx.spring.extractor;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Extracts the authenticated user's username for rate limiting.
 * <p>
 * This extractor retrieves the username from the Spring Security authentication context.
 * It expects the authenticated principal to implement {@link UserDetails}.
 * If the principal is not a {@code UserDetails} instance, it returns {@code null}.
 * </p>
 *
 * <p>Example usage in {@code @RateLimited}:</p>
 * <pre>
 * {@code
 * @RateLimited(key = UsernameExtractor.class, maximumRequests = 5, windowSize = 60)
 * public void someMethod() { ... }
 * }
 * </pre>
 */
@Component
public class UsernameExtractor implements KeyExtractor {

    /**
     * Extracts the username of the currently authenticated user.
     * <p>
     * If the user is authenticated and their principal implements {@link UserDetails},
     * this method returns the username. Otherwise, it returns {@code null}.
     * </p>
     *
     * @return the authenticated user's username, or {@code null} if unavailable
     */
    @Override
    public String extract() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ((principal instanceof UserDetails userDetails)) {
            return userDetails.getUsername();
        }

        return null;
    }
}
