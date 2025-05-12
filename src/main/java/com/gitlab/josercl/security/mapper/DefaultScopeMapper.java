package com.gitlab.josercl.security.mapper;

import com.gitlab.josercl.security.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultScopeMapper implements ScopeMapper {
    private final ScopeMapperExtender scopeMapperExtender;

    public DefaultScopeMapper(ScopeMapperExtender scopeMapperExtender) {
        this.scopeMapperExtender = scopeMapperExtender;
    }

    @Override
    public Collection<GrantedAuthority> mapScopes(Jwt jwt) {
        Collection<GrantedAuthority> scopes = extractScopes(jwt);
        HashSet<GrantedAuthority> result = new HashSet<>(scopes);
        Collection<GrantedAuthority> extraAuthorities = Optional.ofNullable(scopeMapperExtender)
            .map(e -> e.apply(jwt))
            .orElse(List.of());
        result.addAll(extraAuthorities);

        return result;
    }

    private Collection<GrantedAuthority> extractScopes(Jwt jwt) {
        return Optional.ofNullable((String) jwt.getClaim("scope"))
            .map(scopes -> Arrays.asList(scopes.split(" ")))
            .orElse(List.of())
            .stream()
            .map(scope -> new SimpleGrantedAuthority(Constants.SCOPE_PREFIX + scope))
            .collect(Collectors.toSet());
    }
}
