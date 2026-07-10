package com.rafaelsousa.algashop.authorization_server.application;

import com.rafaelsousa.algashop.authorization_server.config.TestSecurityConfig;
import com.rafaelsousa.algashop.authorization_server.config.TestcontainerPostgreSQLConfig;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import({
    TestcontainerPostgreSQLConfig.class,
    TestSecurityConfig.class,
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractApplicationIT {

    @MockitoBean
    private JwtDecoder jwtDecoder;

	@MockitoBean
	private JavaMailSender javaMailSender;
}