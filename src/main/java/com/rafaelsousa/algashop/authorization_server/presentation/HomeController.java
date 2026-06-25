package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.infrastructure.security.AlgaShopSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final AlgaShopSecurityProperties algaShopSecurityProperties;

	@GetMapping("/")
	public String home() {
		return "redirect:" + algaShopSecurityProperties.getDefaultRedirectUri();
	}
}
