package com.rafaelsousa.algashop.authorization_server.application.user.query;

import com.rafaelsousa.algashop.authorization_server.domain.model.AuthUserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserQueryService {
	private final AuthUserRepository authUserRepository;

	public AuthUserOutput findById(UUID userId) {
		return authUserRepository.findById(userId)
			.map(AuthUserOutput::from)
			.orElseThrow(() -> new AuthUserNotFoundException(userId));
	}
}