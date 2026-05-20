package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.security.SecurityCheckApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MyUserController {
	private final AuthUserQueryService authUserQueryService;
	private final SecurityCheckApplicationService securityCheckApplicationService;

	@GetMapping
	public AuthUserOutput getMe() {
		return authUserQueryService.findById(securityCheckApplicationService.getAuthenticatedUserId());
	}
}