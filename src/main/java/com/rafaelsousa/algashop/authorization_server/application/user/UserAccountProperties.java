package com.rafaelsousa.algashop.authorization_server.application.user;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Component
@Validated
@NoArgsConstructor
@ConfigurationProperties("algashop.user-account")
public class UserAccountProperties {

	@NonNull @Valid
	private PasswordToken token;

	@Data
	@NoArgsConstructor
	public static class PasswordToken {

		@NonNull
		private Duration activationTtl;

		@NonNull
		private Duration passwordResetTtl;
	}
}
