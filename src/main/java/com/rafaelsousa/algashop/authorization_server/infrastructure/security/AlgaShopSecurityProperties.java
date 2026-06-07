package com.rafaelsousa.algashop.authorization_server.infrastructure.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@Validated
@NoArgsConstructor
@ConfigurationProperties("algashop.security")
public class AlgaShopSecurityProperties {

	@Valid
	@NotNull
	private CorsProperties cors;

	@Valid
	@NotNull
	private ContentSecurityPolicyProperties csp;

	@Valid
	@NotNull
	private CookieProperties cookie;

	@Data
	@NoArgsConstructor
	public static class CorsProperties {

		@NotEmpty
		private List<String> allowedOrigins = new ArrayList<>();
	}

	@Data
	@NoArgsConstructor
	public static class ContentSecurityPolicyProperties {

		@NotBlank
		private String policyDirectives;
	}

	@Data
	@NoArgsConstructor
	public static class CookieProperties {

		@NotBlank
		private String sameSite;

		@NotBlank
		private String domainName;

		@NotNull
		private Boolean secure;
	}
}