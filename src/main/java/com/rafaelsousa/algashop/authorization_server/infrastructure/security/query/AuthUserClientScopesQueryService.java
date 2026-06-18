package com.rafaelsousa.algashop.authorization_server.infrastructure.security.query;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;

import java.util.Set;

public interface AuthUserClientScopesQueryService {
	Set<String> findAllowedScopesByRoleAndClientId(AuthUserType role, String clientId);
}