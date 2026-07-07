package com.bookmysport.backend.common.Audit;

import com.bookmysport.backend.security.models.SecurityUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("applicationAuditAware")
public class ApplicationAuditAware
        implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return Optional.of("SYSTEM");
        }

        SecurityUser securityUser =
                (SecurityUser) authentication.getPrincipal();

        return Optional.of(
                securityUser.getUsername()
        );
    }
}
