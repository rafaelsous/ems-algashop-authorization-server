package com.rafaelsousa.algashop.authorization_server.application.user.management;

import com.rafaelsousa.algashop.authorization_server.application.user.UserAccountProperties;
import com.rafaelsousa.algashop.authorization_server.application.user.mail.AuthUserMailSender;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserNotFoundException;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserPasswordManager;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserRepository;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.VerificationTokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordManagementApplicationService {
	private final AuthUserRepository authUserRepository;
	private final UserAccountProperties userAccountProperties;
	private final AuthUserPasswordManager passwordManager;
	private final VerificationTokenHasher tokenHasher;
	private final AuthUserMailSender authUserMailSender;

	public void changePasswordWithToken(String plainToken, String newPlainPassword) {
		String hashedToken = tokenHasher.hash(plainToken);
		AuthUser authUser = authUserRepository.findByVerificationToken(hashedToken)
			.orElseThrow(() -> new AuthUserNotFoundException("User not found by verification token"));

		try {
			authUser.changePasswordWithToken(plainToken, newPlainPassword, passwordManager, tokenHasher);
		} catch (IllegalArgumentException | IllegalStateException ex) {
            throw new AccessDeniedException(ex.getMessage());
		}

		authUserRepository.save(authUser);
	}

	public void requestPasswordChange(UUID userId) {
		AuthUser authUser = authUserRepository.findById(userId)
			.orElseThrow(() -> new AuthUserNotFoundException(userId));

		requestPasswordChange(authUser);
	}

	public void requestPasswordChange(String email) {
		AuthUser authUser = authUserRepository.findByEmail(email)
			.orElseThrow(() -> new AuthUserNotFoundException(email));

		requestPasswordChange(authUser);
	}

	private void requestPasswordChange(AuthUser authUser) {
		String plainToken = authUser.generateVerificationToken(userAccountProperties.getToken().getPasswordResetTtl(), tokenHasher);
		authUserMailSender.sendPasswordChangeEmail(authUser, plainToken);

		authUserRepository.save(authUser);
	}
}
