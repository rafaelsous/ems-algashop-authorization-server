package com.rafaelsousa.algashop.authorization_server.domain.model.user;


import java.util.UUID;

public class AuthUserTestDataBuilder {
    private AuthUserTestDataBuilder() {}

    public static AuthUser.AuthUserBuilder aUser() {
        return AuthUser.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("john.doe@email.com")
            .type(AuthUserType.MANAGER)
            .enabled(true);
    }

    public static AuthUser.AuthUserBuilder aUserAlt() {
        return AuthUser.builder()
            .id(UUID.randomUUID())
            .name("Silvester Stalone")
            .email("silvester.stalone@email.com")
            .type(AuthUserType.OPERATOR)
            .enabled(true);
    }
}