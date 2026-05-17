package com.rafaelsousa.algashop.authorization_server.application.user.management;

import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.domain.model.AuthUser;
import com.rafaelsousa.algashop.authorization_server.domain.model.AuthUserRepository;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserManagementApplicationService {
	private final PasswordEncoder passwordEncoder;
	private final AuthUserRepository authUserRepository;

	public AuthUserOutput create(AuthUserInput authUserInput) {
		if (authUserRepository.existsByEmail(authUserInput.getEmail())) {
			throw new AuthUserEmailAlreadyInUseException("Email already exists");
		}

		String tempPassword = RandomStringUtils.secure().nextAlphanumeric(12); // TODO: send via email

		String passwordHash = passwordEncoder.encode(tempPassword);

		AuthUser user = AuthUser.brandNew(
			authUserInput.getName(),
			authUserInput.getEmail(),
			authUserInput.getType(),
			passwordHash
		);

		return AuthUserOutput.from(authUserRepository.save(user));
	}
}