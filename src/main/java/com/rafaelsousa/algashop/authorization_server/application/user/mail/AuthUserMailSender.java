package com.rafaelsousa.algashop.authorization_server.application.user.mail;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser;

public interface AuthUserMailSender {
	void sendActivationEmail(AuthUser user, String activationToken);
	void sendPasswordChangeEmail(AuthUser user, String activationToken);
}
