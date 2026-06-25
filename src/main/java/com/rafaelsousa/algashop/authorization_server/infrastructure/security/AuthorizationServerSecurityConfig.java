package com.rafaelsousa.algashop.authorization_server.infrastructure.security;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.code.DelegatingAuthorizationCodeRequestValidator;
import com.rafaelsousa.algashop.authorization_server.infrastructure.security.oidc.OidcUserInfoMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.web.authentication.OidcLogoutAuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthorizationServerSecurityConfig {
    public static final String LOGIN_URL = "/login";

    private final OidcUserInfoMapper userInfoMapper;
    private final AlgaShopSecurityProperties algaShopSecurityProperties;
    private final OidcLogoutAuthenticationSuccessHandler oidcLogoutAuthenticationSuccessHandler;
    private final DelegatingAuthorizationCodeRequestValidator delegatingAuthorizationCodeRequestValidator;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerFilterChain(HttpSecurity http) {
        OAuth2AuthorizationServerConfigurer authorizationServer =
                new OAuth2AuthorizationServerConfigurer();

        http.securityMatcher(authorizationServer.getEndpointsMatcher())
            .cors(Customizer.withDefaults())
            .headers(headers -> {
                AlgaShopSecurityProperties.ContentSecurityPolicyProperties csp = algaShopSecurityProperties.getCsp();
                headers.contentSecurityPolicy(c -> c.policyDirectives(csp.getPolicyDirectives()));
            })
            .with(authorizationServer, configurer -> configurer
                .oidc(oidc -> oidc
                    .logoutEndpoint(logout ->
                        logout.logoutResponseHandler(oidcLogoutAuthenticationSuccessHandler))
                    .userInfoEndpoint(userInfo ->
                        userInfo.userInfoMapper(userInfoMapper)))
                .authorizationEndpoint(endpoint ->
                    endpoint.authenticationProviders(this::customizeAuthenticationProviders)
                        .consentPage("/oauth2/consent")
                )
            )
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
            .exceptionHandling(
                    exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                                    new LoginUrlAuthenticationEntryPoint(LOGIN_URL),
                                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) {
        http.securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**").permitAll()
                .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(LOGIN_URL, "/forgot-password", "/css/**", "/js/**", "/img/**", "/favicon.ico")
                    .permitAll()
                .anyRequest().authenticated())
            .formLogin(c -> c.loginPage(LOGIN_URL).permitAll());

        return http.build();
    }

    private void customizeAuthenticationProviders(List<AuthenticationProvider> authenticationProviders) {
        authenticationProviders.stream()
            .filter(OAuth2AuthorizationCodeRequestAuthenticationProvider.class::isInstance)
            .map(OAuth2AuthorizationCodeRequestAuthenticationProvider.class::cast)
            .forEach(provider ->
                provider.setAuthenticationValidator(delegatingAuthorizationCodeRequestValidator));
    }
}