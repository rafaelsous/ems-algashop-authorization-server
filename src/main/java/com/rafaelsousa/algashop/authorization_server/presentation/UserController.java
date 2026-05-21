package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserInput;
import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserManagementApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanWriteUsers;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final AuthUserManagementApplicationService authUserManagementApplicationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CanWriteUsers
	public AuthUserOutput createUser(@Valid @RequestBody AuthUserInput authUserInput) {
		return authUserManagementApplicationService.create(authUserInput);
	}
}