package com.rafaelsousa.algashop.authorization_server.infrastructure.security.token;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.query.AuthUserClientScopesQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopePolicyService {
	private final AuthUserClientScopesQueryService scopesQueryService;

	public Set<String> resolveScopes(AuthUserType role, String clientId, Set<String> authorizedScopes) {
		Set<String> allowedscopes = scopesQueryService.findAllowedScopesByRoleAndClientId(role, clientId);

		if (authorizedScopes.isEmpty()) {
			return authorizedScopes;
		}

		return authorizedScopes.stream()
			.filter(allowedscopes::contains)
			.collect(Collectors.toSet());
	}
}