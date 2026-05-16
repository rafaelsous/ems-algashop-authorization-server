package com.rafaelsousa.algashop.authorization_server.infrastructure.persistence;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.query.OAuth2AuthorizationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcOAuth2AuthorizationQueryService implements OAuth2AuthorizationQueryService {
	private final JdbcOperations jdbcOperations;

	private static final String SQL = "select id from oauth2_authorization where principal_name = ?";

	@Override
	public List<String> findAuthorizationIds(String principalName) {
		return jdbcOperations.queryForList(SQL, String.class, principalName);
	}
}