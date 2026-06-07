package com.rafaelsousa.algashop.authorization_server.infrastructure.security.cookie;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.AlgaShopSecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class CookieConfig {

	@Bean
	public CookieSerializer cookieSerializer(AlgaShopSecurityProperties properties) {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName("JSESSIONID");
		serializer.setCookiePath("/");
		serializer.setUseHttpOnlyCookie(true);

		AlgaShopSecurityProperties.CookieProperties cookie = properties.getCookie();
		serializer.setSameSite(cookie.getSameSite());
		serializer.setDomainName(cookie.getDomainName());
		serializer.setUseSecureCookie(cookie.getSecure());

		return serializer;
	}
}