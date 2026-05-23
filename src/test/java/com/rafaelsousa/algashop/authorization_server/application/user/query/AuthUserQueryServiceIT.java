package com.rafaelsousa.algashop.authorization_server.application.user.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rafaelsousa.algashop.authorization_server.application.AbstractApplicationIT;
import com.rafaelsousa.algashop.authorization_server.domain.model.IdGenerator;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser.AuthUserBuilder;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserRepository;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserTestDataBuilder;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

class AuthUserQueryServiceIT extends AbstractApplicationIT {
	private final AuthUserRepository authUserRepository;
	private final AuthUserQueryService authUserQueryService;

	@Autowired
	public AuthUserQueryServiceIT(AuthUserRepository authUserRepository, AuthUserQueryService authUserQueryService) {
		this.authUserRepository = authUserRepository;
		this.authUserQueryService = authUserQueryService;
	}

	@Test
    void shouldFindBydId() {
		AuthUserBuilder authUserTestDataBuilder = AuthUserTestDataBuilder.aUser();
		AuthUser authUser = authUserTestDataBuilder.name("Rafael Sousa")
			.email("rafael.sousa@email.com")
			.type(AuthUserType.MANAGER)
			.enabled(true)
			.build();

		authUserRepository.saveAndFlush(authUser);

        AuthUserOutput authUserOutput = authUserQueryService.findById(authUser.getId());

		assertThat(authUserOutput)
			.satisfies(auo -> {
				assertThat(auo.getId()).isEqualTo(authUser.getId());
				assertThat(auo.getName()).isEqualTo(authUser.getName());
				assertThat(auo.getEmail()).isEqualTo(authUser.getEmail());
				assertThat(auo.getType()).isEqualTo(authUser.getType());
				assertThat(auo.isEnabled()).isEqualTo(authUser.isEnabled());
		});
	}

    @Test
    void shouldThrowExceptionWhenNotFoundUser() {
	    UUID userId = IdGenerator.generateTimeBasedUUID();

	    assertThatThrownBy(() -> authUserQueryService.findById(userId))
		    .isInstanceOf(AuthUserNotFoundException.class)
		    .hasMessage(String.format("User not found with ID: %s", userId));
    }

    @Test
    void shouldFilterUsersByEmail() {
	    AuthUser john = AuthUserTestDataBuilder.aUser().build();
	    AuthUser silvester = AuthUserTestDataBuilder.aUserAlt().build();

		authUserRepository.saveAllAndFlush(List.of(john, silvester));

	    AuthUserFilter authUserFilter = new AuthUserFilter();
		authUserFilter.setEmail(john.getEmail());

	    PageModel<AuthUserOutput> filtered = authUserQueryService.filter(authUserFilter);

		assertThat(filtered).satisfies(page -> {
			assertThat(page.getContent()).isNotNull();
			assertThat(page.getTotalPages()).isEqualTo(1);
			assertThat(page.getTotalElements()).isEqualTo(1);
			assertThat(page.getContent().getFirst().getEmail()).isEqualTo(john.getEmail());
		});
    }

    @Test
    void shouldFilterUsersByType() {
	    AuthUser john = AuthUserTestDataBuilder.aUser().build();
	    AuthUser silvester = AuthUserTestDataBuilder.aUserAlt().build();

	    authUserRepository.saveAllAndFlush(List.of(john, silvester));

	    AuthUserFilter authUserFilter = new AuthUserFilter();
	    AuthUserType type = john.getType();
	    authUserFilter.setType(type);

	    PageModel<AuthUserOutput> filtered = authUserQueryService.filter(authUserFilter);

	    assertThat(filtered).satisfies(page -> {
		    assertThat(page.getContent()).isNotNull();
		    assertThat(page.getContent().getFirst().getType()).isEqualTo(type);
	    });
    }
}