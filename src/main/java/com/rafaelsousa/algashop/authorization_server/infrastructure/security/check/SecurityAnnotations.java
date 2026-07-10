package com.rafaelsousa.algashop.authorization_server.infrastructure.security.check;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SecurityAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_users:read') and not hasRole('CUSTOMER')")
    public @interface CanReadUsers {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_users:write') and not hasRole('CUSTOMER')")
    public @interface CanWriteUsers {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("@securityChecks.canAccessOwnProfile()")
    public @interface CanAccessOwnProfile {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("@securityChecks.canAccessOwnProfile() and hasRole('CUSTOMER')")
    public @interface CanAnonymizeOwnProfile {}
}