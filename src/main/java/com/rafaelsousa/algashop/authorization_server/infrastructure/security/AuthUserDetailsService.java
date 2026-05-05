package com.rafaelsousa.algashop.authorization_server.infrastructure.security;

import com.rafaelsousa.algashop.authorization_server.domain.model.AuthUser;
import com.rafaelsousa.algashop.authorization_server.domain.model.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
	private final AuthUserRepository authUserRepository;

	@Override
	@NullMarked
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AuthUser user = authUserRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found " + email));

		return User.withUsername(user.getEmail()).password(user.getPassword())
			.disabled(!user.isEnabled()).roles("USER").build();
	}
}