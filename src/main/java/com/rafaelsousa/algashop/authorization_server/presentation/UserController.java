package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserUpdateInput;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserFilter;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserQueryService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.PageModel;
import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserInput;
import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserManagementApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.AuthUserOutput;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanReadUsers;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.check.SecurityAnnotations.CanWriteUsers;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final AuthUserQueryService authUserQueryService;
	private final AuthUserManagementApplicationService authUserManagementApplicationService;

	@CanWriteUsers
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AuthUserOutput createUser(@RequestBody @Valid AuthUserInput authUserInput) {
		return authUserManagementApplicationService.create(authUserInput);
	}

	@CanReadUsers
	@GetMapping
	public PageModel<AuthUserOutput> filter(AuthUserFilter filter) {
		return authUserQueryService.filter(filter);
	}

	@CanReadUsers
	@GetMapping("/{userId}")
	public AuthUserOutput getUser(@PathVariable UUID userId) {
		return authUserQueryService.findById(userId);
	}

	@CanWriteUsers
	@PutMapping("/{userId}")
	public AuthUserOutput update(@PathVariable UUID userId, @RequestBody @Valid AuthUserUpdateInput authUserUpdateInput) {
		return authUserManagementApplicationService.update(userId, authUserUpdateInput);
	}

	@CanWriteUsers
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void anonymize(@PathVariable UUID userId) {
		authUserManagementApplicationService.delete(userId);
	}
}