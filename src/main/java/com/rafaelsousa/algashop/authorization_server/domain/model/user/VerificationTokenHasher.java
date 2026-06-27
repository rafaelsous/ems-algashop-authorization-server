package com.rafaelsousa.algashop.authorization_server.domain.model.user;

public interface VerificationTokenHasher {
	String generates();
	String hash(String plainToken);
	boolean isEqual(String hashedToken, String plainToken);
}
