package com.rafaelsousa.algashop.authorization_server.application.user.management;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityChecks;
import com.rafaelsousa.algashop.authorization_server.application.user.UserAccountProperties;
import com.rafaelsousa.algashop.authorization_server.application.user.mail.AuthUserMailSender;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserNotFoundException;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.*;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserManagementApplicationService {
    private final AuthUserRepository authUserRepository;
    private final SecurityChecks securityChecks;
    private final VerificationTokenHasher tokenHasher;
    private final UserAccountProperties userAccountProperties;
    private final AuthUserPasswordManager authUserPasswordManager;
    private final AuthUserMailSender authUserMailSender;

    public AuthUserOutput create(AuthUserInput authUserInput) {
        if (!securityChecks.canRegisterUserOfType(authUserInput.getType())) {
            throw new AccessDeniedException(
                    "Cannot register user of type " + authUserInput.getType());
        }

        if (authUserRepository.existsByEmail(authUserInput.getEmail())) {
            throw new AuthUserEmailAlreadyInUseException("Email already exists");
        }

        AuthUser user =
                AuthUser.brandNew(
                        authUserInput.getName(),
                        authUserInput.getEmail(),
                        authUserInput.getType(),
                        authUserPasswordManager);

        String plainToken =
                user.generateVerificationToken(
                        userAccountProperties.getToken().getActivationTtl(), tokenHasher);

        authUserMailSender.sendActivationEmail(user, plainToken);

        return AuthUserOutput.from(authUserRepository.save(user));
    }

    public AuthUserOutput update(UUID userId, AuthUserUpdateInput input) {
        AuthUser user = findAuthUserOrFail(userId);

        verifyCanEditUser(user, input);

        user.setName(input.getName());
        user.setType(input.getType());
        user.setEnabled(input.isEnabled());

        return AuthUserOutput.from(authUserRepository.save(user));
    }

    public AuthUserOutput updateMyProfile(UUID userId, MyProfileUpdateInput input) {
        AuthUser user = findAuthUserOrFail(userId);

        if (!securityChecks.canEditUser(user.getType(), user.getId())) {
            throw new AccessDeniedException("Cannot edit user");
        }

        user.setName(input.getName());

        return AuthUserOutput.from(authUserRepository.save(user));
    }

    public void delete(UUID userId) {
        AuthUser user = findAuthUserOrFail(userId);

        verifyCanAnonymizeOwnProfile(user);

        user.anonymize();
        authUserRepository.save(user);
    }

    private void verifyCanEditUser(AuthUser authUser, AuthUserUpdateInput authUserUpdateInput) {
        if (!securityChecks.canEditUser(authUser.getType(), authUser.getId())) {
            throw new AccessDeniedException("Cannot edit user of type " + authUser.getType());
        }

        if (!securityChecks.canChangeUserType(authUser.getType(), authUserUpdateInput.getType())) {
            throw new AccessDeniedException(
                    "Cannot change user type to " + authUserUpdateInput.getType());
        }
    }

    private @NonNull AuthUser findAuthUserOrFail(UUID userId) {
        return authUserRepository
                .findById(userId)
                .orElseThrow(() -> new AuthUserNotFoundException(userId));
    }

    private void verifyCanAnonymizeOwnProfile(AuthUser user) {
        if (securityChecks.getAuthenticatedUserId().equals(user.getId()) && AuthUserType.CUSTOMER != user.getType()) {
            throw new AccessDeniedException("Only users of type CUSTOMER can delete their own profile");
        }
    }
}
