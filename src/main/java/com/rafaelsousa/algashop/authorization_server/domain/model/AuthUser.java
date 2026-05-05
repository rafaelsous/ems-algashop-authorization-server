package com.rafaelsousa.algashop.authorization_server.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "auth_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AuthUser extends AbstractAuditableAggregateRoot<AuthUser>{

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	private String name;
	private String email;
	private String password;
	private boolean enabled;

	@Enumerated(EnumType.STRING)
	private AuthUserType type;
}