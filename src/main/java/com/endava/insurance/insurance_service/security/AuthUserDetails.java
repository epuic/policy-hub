package com.endava.insurance.insurance_service.security;

import com.endava.insurance.insurance_service.domain.enums.AdministratorRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private final String email;
    private final String passwordHash;
    private final List<GrantedAuthority> authorities;
    private final Long entityId;
    private final String entityType;

    public static AuthUserDetails forAdministrator(String email, String passwordHash, AdministratorRole role, Long id) {
        String authority = "ROLE_" + role.name();
        return new AuthUserDetails(email, passwordHash, List.of(new SimpleGrantedAuthority(authority)), id, "ADMIN");
    }

    public static AuthUserDetails forBroker(String email, String passwordHash, Long id) {
        return new AuthUserDetails(email, passwordHash,
                List.of(new SimpleGrantedAuthority("ROLE_BROKER")), id, "BROKER");
    }

    private AuthUserDetails(String email, String passwordHash, List<GrantedAuthority> authorities, Long entityId, String entityType) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.authorities = authorities;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }
}
