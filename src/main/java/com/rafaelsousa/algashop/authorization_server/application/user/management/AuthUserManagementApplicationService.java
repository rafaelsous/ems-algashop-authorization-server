package com.rafaelsousa.algashop.authorization_server.application.user.management;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityChecks;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserNotFoundException;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserRepository;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserManagementApplicationService {
	private final PasswordEncoder passwordEncoder;
	private final AuthUserRepository authUserRepository;
	private final SecurityChecks securityChecks;

	public AuthUserOutput create(AuthUserInput authUserInput) {
		if (!securityChecks.canRegisterUserOfType(authUserInput.getType())) {
			throw new AccessDeniedException("Cannot register user of type " + authUserInput.getType());
		}

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

	public AuthUserOutput update(UUID userId, AuthUserUpdateInput input) {
		AuthUser user = authUserRepository.findById(userId).orElseThrow(() -> new AuthUserNotFoundException(userId));

		verifyCanEditUser(user, input);

		user.setName(input.getName());
		user.setType(input.getType());
		user.setEnabled(input.isEnabled());

		return AuthUserOutput.from(authUserRepository.save(user));
	}

	public void delete(UUID userId) {
		AuthUser user = authUserRepository.findById(userId)
			.orElseThrow(() -> new AuthUserNotFoundException(userId));

		user.anonymize();
		authUserRepository.save(user);
	}

	private void verifyCanEditUser(AuthUser authUser, AuthUserUpdateInput authUserUpdateInput) {
		if (!securityChecks.canEditUser(authUser.getType(), authUser.getId())) {
			throw new AccessDeniedException("Cannot edit user of type " + authUser.getType());
		}

		if (!securityChecks.canChangeUserType(authUser.getType(), authUserUpdateInput.getType())) {
			throw new AccessDeniedException("Cannot change user type to " + authUserUpdateInput.getType());
		}
	}
}