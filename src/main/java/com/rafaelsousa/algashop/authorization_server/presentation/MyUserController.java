package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityChecks;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserQueryService;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanAccessOwnProfile;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MyUserController {
	private final AuthUserQueryService authUserQueryService;
	private final SecurityChecks securityChecks;

	@GetMapping
	@CanAccessOwnProfile
	public AuthUserOutput getMe() {
		return authUserQueryService.findById(securityChecks.getAuthenticatedUserId());
	}
}