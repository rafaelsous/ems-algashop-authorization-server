package com.rafaelsousa.algashop.authorization_server.infrastructure.security.oidc;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.query.OAuth2AuthorizationQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OidcRevokeAuthorizationsLogoutHandler implements LogoutHandler {
	private final OAuth2AuthorizationService oAuth2AuthorizationService;
	private final OAuth2AuthorizationQueryService oAuth2AuthorizationQueryService;
	private final SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();

	@Override
	public void logout(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
	                   @Nullable Authentication authentication) {
		if (authentication == null || authentication.getName() == null) {
			return;
		}

		performLogout(request, response, authentication);
		revokeAuthorizations(authentication);
	}

	private void performLogout(HttpServletRequest request, HttpServletResponse response,
	                           Authentication authentication) {
		OidcLogoutAuthenticationToken oidcLogoutAuthentication = (OidcLogoutAuthenticationToken) authentication;

		// Check for active user session
		if (oidcLogoutAuthentication.isPrincipalAuthenticated()) {
			this.securityContextLogoutHandler.logout(request, response,
				(Authentication) oidcLogoutAuthentication.getPrincipal());
		}
	}

	private void revokeAuthorizations(@Nullable Authentication authentication) {
		assert authentication != null;
		String email = authentication.getName();

		List<String> authorizationIds = oAuth2AuthorizationQueryService.findAuthorizationIds(email);
		for (String authorizationId : authorizationIds) {
			OAuth2Authorization authorization = oAuth2AuthorizationService.findById(authorizationId);

			if (authorization != null) {
				oAuth2AuthorizationService.remove(authorization);
			}
		}
	}
}