package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityChecks;
import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserManagementApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.management.MyProfileUpdateInput;
import com.rafaelsousa.algashop.authorization_server.application.user.management.PasswordManagementApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserQueryService;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanAccessOwnProfile;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanAnonymizeOwnProfile;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MyUserController {
    private final SecurityChecks securityChecks;
    private final AuthUserQueryService authUserQueryService;
    private final PasswordManagementApplicationService passwordManagementApplicationService;
    private final AuthUserManagementApplicationService authUserManagementApplicationService;

    @GetMapping
    @CanAccessOwnProfile
    public AuthUserOutput getMe() {
        return authUserQueryService.findById(securityChecks.getAuthenticatedUserId());
    }

    @PostMapping("/password-change")
    @CanAccessOwnProfile
    public void requestPasswordChange() {
        passwordManagementApplicationService.requestPasswordChange(
                securityChecks.getAuthenticatedUserId());
    }

    @PutMapping
    @CanAccessOwnProfile
    public AuthUserOutput updateMe(@RequestBody @Valid MyProfileUpdateInput myProfileUpdateInput) {
        return authUserManagementApplicationService.updateMyProfile(
                securityChecks.getAuthenticatedUserId(), myProfileUpdateInput);
    }

    @DeleteMapping
    @CanAnonymizeOwnProfile
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anonymizeMe() {
        authUserManagementApplicationService.delete(securityChecks.getAuthenticatedUserId());
    }
}
