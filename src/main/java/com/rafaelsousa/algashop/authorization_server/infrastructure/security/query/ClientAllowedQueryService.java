package com.rafaelsousa.algashop.authorization_server.infrastructure.security.query;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;

import java.util.Set;

public interface ClientAllowedQueryService {
	Set<String> findByRole(AuthUserType role);
}