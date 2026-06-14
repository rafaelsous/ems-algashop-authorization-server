package com.rafaelsousa.algashop.authorization_server.infrastructure.security.check;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityCheckApplicationService;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service("securityCheck")
public class OAuth2SecurityCheckApplicationServiceImpl implements SecurityCheckApplicationService {
	private static final String SCOPE_USERS_WRITE = "SCOPE_users:write";
	private static final String ROLE_MANAGER = "ROLE_" + AuthUserType.MANAGER.name();

	@Override
    public UUID getAuthenticatedUserId() {
        if (isMachineAuthenticated()) {
            throw new AccessDeniedException("Machine users do not have user ID");
        }

        Jwt jwt = getJwt();


	    try {
		    return UUID.fromString(jwt.getSubject());
	    } catch (IllegalArgumentException ex) {
            log.error("Invalid user ID in JWT subject: {}", jwt.getSubject(), ex);
            throw new AccessDeniedException("Invalid user ID in JWT subject");
	    }
    }

    @Override
    public boolean isAuthenticated() {
	    try {
		    return getAuthentication().isAuthenticated();
	    } catch (IllegalStateException ex) {
            log.debug(ex.getMessage(), ex);
		    return false;
	    }
    }

    @Override
    public boolean isMachineAuthenticated() {
        Jwt jwt;

        try {
		    jwt = getJwt();
        } catch (IllegalStateException ex) {
            log.debug(ex.getMessage(), ex);
            return false;
        }

        return jwt.getAudience().contains(jwt.getSubject());
    }

    @Override
    public boolean canAccessOwnProfile() {
        return this.isAuthenticated() && !this.isMachineAuthenticated();
    }

    @Override
    public boolean canRegisterUserOfType(AuthUserType registrationType) {
		if (!isAuthenticated()) {
			return false;
		}

        if (!hasAuthority(SCOPE_USERS_WRITE)) {
			return false;
        }

		if (registrationType == AuthUserType.CUSTOMER) {
			return isMachineAuthenticated();
		}

		if (hasAuthority(ROLE_MANAGER)) {
			return registrationType == AuthUserType.MANAGER || registrationType.equals(AuthUserType.OPERATOR);
		}

        return false;
    }

	@Override
	public boolean canEditUser(AuthUserType editType, UUID editUserId) {
		if (isMachineAuthenticated()) {
			return false;
		}

		try {
			if (getAuthenticatedUserId().equals(editUserId)) return true;
		} catch (AccessDeniedException _) {
			return false;
		}

		if (hasAuthority(ROLE_MANAGER)) {
			return editType == AuthUserType.MANAGER || editType.equals(AuthUserType.OPERATOR);
		}

		return false;
	}

	@Override
	public boolean canChangeUserType(AuthUserType currentUserType, AuthUserType newUserType) {
		if (currentUserType == newUserType) return true;

		if (hasAuthority(ROLE_MANAGER)) {
			if (currentUserType == AuthUserType.MANAGER && newUserType == AuthUserType.OPERATOR) {
				return true;
			}

			return currentUserType == AuthUserType.OPERATOR && newUserType == AuthUserType.MANAGER;
		}

		return false;
	}

    private boolean hasAuthority(String rawAuthority) {
        Authentication authentication;
	    try {
		    authentication = getAuthentication();
	    } catch (IllegalStateException ex) {
            log.debug(ex.getMessage(), ex);
		    return false;
	    }

        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(rawAuthority));
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found");
        }

        return authentication;
    }

    private Jwt getJwt() {
        Authentication authentication = getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        throw new IllegalStateException("Authentication principal is not a JWT");
    }
}