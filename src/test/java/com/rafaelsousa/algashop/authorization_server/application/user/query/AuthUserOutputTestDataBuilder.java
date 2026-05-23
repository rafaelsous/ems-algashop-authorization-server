package com.rafaelsousa.algashop.authorization_server.application.user.query;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;

import java.util.UUID;

public class AuthUserOutputTestDataBuilder {
    private AuthUserOutputTestDataBuilder() {}

    public static AuthUserOutput.AuthUserOutputBuilder aUser() {
        return AuthUserOutput.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("john.doe@email.com")
            .type(AuthUserType.MANAGER)
            .enabled(true);
    }

    public static AuthUserOutput.AuthUserOutputBuilder aUserAlt() {
        return AuthUserOutput.builder()
            .id(UUID.randomUUID())
            .name("Silvester Stalone")
            .email("silvester.stalone@email.com")
            .type(AuthUserType.OPERATOR)
            .enabled(true);
    }
}