package com.rafaelsousa.algashop.authorization_server.contract.base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserManagementApplicationService;
import com.rafaelsousa.algashop.authorization_server.application.user.query.*;
import com.rafaelsousa.algashop.authorization_server.application.user.query.PageModel;
import com.rafaelsousa.algashop.authorization_server.presentation.UserController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = UserController.class)
class UserBase {
    private static final UUID validUserId = UUID.fromString("019d7764-3b02-7be2-9112-039fda30e965");
    private static final UUID invalidUserId = UUID.fromString("019a0215-078c-7827-9bbe-e28d5402a5d4");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private AuthUserQueryService authUserQueryService;

    @MockitoBean
    private AuthUserManagementApplicationService userManagementApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();


        mockFilterUsers();
        mockFindByIdValidUserId();
        mockFindByIdInvalidUserId();
        mockDeleteInvlidUserId();
    }

    private void mockFilterUsers() {
        when(authUserQueryService.filter(any(AuthUserFilter.class)))
                .then(answer -> {
                    AuthUserFilter filter = answer.getArgument(0);

                    return PageModel.<AuthUserOutput>builder()
                            .number(0)
                            .size(filter.getSize())
                            .totalElements(2)
                            .content(
                                    List.of(
                                            AuthUserOutputTestDataBuilder.aUser().build(),
                                            AuthUserOutputTestDataBuilder.aUserAlt().build()
                                    )
                            ).build();
                });
    }

    private void mockFindByIdValidUserId() {
        when(authUserQueryService.findById(validUserId))
            .thenReturn(AuthUserOutputTestDataBuilder.aUser().id(validUserId).build());
    }

    private void mockFindByIdInvalidUserId() {
        when(authUserQueryService.findById(invalidUserId))
                .thenThrow(new AuthUserNotFoundException(invalidUserId));
    }

    private void mockDeleteInvlidUserId() {
        doThrow(new AuthUserNotFoundException(invalidUserId))
            .when(userManagementApplicationService)
            .delete(invalidUserId);
    }
}