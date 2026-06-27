package com.rafaelsousa.algashop.authorization_server.domain.model.user;

import com.rafaelsousa.algashop.authorization_server.domain.model.AbstractAuditableAggregateRoot;
import com.rafaelsousa.algashop.authorization_server.domain.model.DomainException;
import com.rafaelsousa.algashop.authorization_server.domain.model.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "auth_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthUser extends AbstractAuditableAggregateRoot<AuthUser> {

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	private String name;
	private String email;
	private String password;

	@Setter
	private boolean enabled;
	private boolean emailVerified;

	private String verificationToken;
	private OffsetDateTime verificationTokenExpirationDate;

	@Enumerated(EnumType.STRING)
	private AuthUserType type;

	public static AuthUser brandNew(String name, String email, AuthUserType type, AuthUserPasswordManager passwordManager) {
		AuthUser user = new AuthUser();

		user.setId(IdGenerator.generateTimeBasedUUID());
		user.setName(name);
		user.setEmail(email);
		user.setType(type);
        user.setPassword(passwordManager.encrypt(passwordManager.generates()));
		user.setEnabled(true);
		user.setEmailVerified(false);

		return user;
	}

	public void setName(String name) {
        if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
        }

		this.name = name;
	}

	public void setType(AuthUserType type) {
		Objects.requireNonNull(type);

		if (this.type == AuthUserType.CUSTOMER) {
            throw new DomainException("Cannot change type of a CUSTOMER user");
		}

		this.type = type;
	}

	private void setId(UUID id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	public void anonymize() {
		this.setName("Anonymized User");
		this.setEmail("anonymized-" + this.id + "@deleted.local");
		this.setEnabled(false);
	}

	public String generateVerificationToken(Duration expiresIn, VerificationTokenHasher hasher) {
		String plainToken = hasher.generates();
		this.verificationToken = hasher.hash(plainToken);
		this.verificationTokenExpirationDate = OffsetDateTime.now(ZoneId.systemDefault()).plus(expiresIn);

		return plainToken;
	}

    public void changePasswordWithToken(
            String plainToken,
            String plainPassword,
            AuthUserPasswordManager passwordManager,
            VerificationTokenHasher tokenHasher) {
		verifyToken(plainToken, tokenHasher);
		setPassword(passwordManager.encrypt(plainPassword));
		cleanVerificationToken();

		if (!isEmailVerified()) {
			setEmailVerified(true);
		}
    }

	public boolean isDisabled() {
		return !isEmailVerified() || !isEnabled();
	}

	private void setEmail(String email) {
		if (StringUtils.isBlank(email)) {
			throw new IllegalArgumentException();
		}

		this.email = email;
	}

	private void setPassword(String password) {
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException();
		}

		this.password = password;
	}

	private void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	private void verifyToken(String plainToken, VerificationTokenHasher tokenHasher) {
		if (StringUtils.isBlank(plainToken)) {
			throw new IllegalArgumentException("Token cannot be blank");
		}

		if (StringUtils.isBlank(this.verificationToken)) {
			throw new DomainException("No verification token set for this user");
		}

		if (isTokenExpired()) {
			throw new IllegalArgumentException("Verification token has expired");
		}

		if (!tokenHasher.isEqual(this.verificationToken, plainToken)) {
            throw new IllegalArgumentException("Invalid verification token");
		}
	}

	private boolean isTokenExpired() {
		return this.verificationTokenExpirationDate == null || OffsetDateTime.now(ZoneId.systemDefault()).isAfter(this.verificationTokenExpirationDate);
	}

	private void cleanVerificationToken() {
		this.verificationToken = null;
		this.verificationTokenExpirationDate = null;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		AuthUser authUser = (AuthUser) o;
		return getId() != null && Objects.equals(getId(), authUser.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy hibernateProxy ?hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}