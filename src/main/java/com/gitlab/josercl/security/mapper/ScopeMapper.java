package com.gitlab.josercl.security.mapper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public interface ScopeMapper {
    Collection<GrantedAuthority> mapScopes(Jwt jwt);
}
