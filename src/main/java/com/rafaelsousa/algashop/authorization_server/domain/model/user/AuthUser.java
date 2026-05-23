package com.rafaelsousa.algashop.authorization_server.domain.model.user;

import com.rafaelsousa.algashop.authorization_server.domain.model.AbstractAuditableAggregateRoot;
import com.rafaelsousa.algashop.authorization_server.domain.model.DomainException;
import com.rafaelsousa.algashop.authorization_server.domain.model.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;

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

	@Enumerated(EnumType.STRING)
	private AuthUserType type;

	public static AuthUser brandNew(String name, String email, AuthUserType type, String passwordHash) {
		AuthUser user = new AuthUser();

		user.setId(IdGenerator.generateTimeBasedUUID());
		user.setName(name);
		user.setEmail(email);
		user.setType(type);
		user.setPassword(passwordHash);
		user.setEnabled(true);

		return user;
	}

	public void setName(String name) {
        if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
        }

		this.name = name;
	}

	public void setPassword(String password) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}

		this.password = password;
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

	private void setEmail(String email) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException();
		}

		this.email = email;
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