package com.rafaelsousa.algashop.authorization_server.infrastructure.security.cors;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.AlgaShopSecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource(AlgaShopSecurityProperties properties) {
		CorsConfiguration cors = new CorsConfiguration();

        cors.setAllowedOrigins(properties.getCors().getAllowedOrigins());
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		cors.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);

		return source;
	}
}