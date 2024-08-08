package com.gitlab.josercl.security.mapper;

import com.gitlab.josercl.security.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class DefaultRoleMapper implements RoleMapper {
    public static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    public static final String REALM_ACCESS_CLAIM = "realm_access";

    private final String clientId;
    private final RoleMapperExtender roleMapperExtender;

    public DefaultRoleMapper(String clientId, RoleMapperExtender roleMapperExtender) {
        this.clientId = clientId;
        this.roleMapperExtender = roleMapperExtender;
    }

    @Override
    public Collection<GrantedAuthority> mapRoles(Jwt jwt) {
        Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
        Collection<GrantedAuthority> resourcesRoles = extractResourcesRoles(jwt);
        HashSet<GrantedAuthority> result = new HashSet<>(realmRoles);
        result.addAll(resourcesRoles);
        Collection<GrantedAuthority> extraAuthorities = Optional.ofNullable(roleMapperExtender)
            .map(e -> e.apply(jwt))
            .orElse(List.of());
        result.addAll(extraAuthorities);

        return result;
    }

    private Collection<GrantedAuthority> extractResourcesRoles(Jwt jwt) {
        return Optional.ofNullable((Map<String, Object>) jwt.getClaim(RESOURCE_ACCESS_CLAIM))
            .map(resourceAccess -> (Map<String, Object>) resourceAccess.get(clientId))
            .map(resource -> (List<String>) resource.get("roles"))
            .orElse(List.of())
            .stream()
            .map(role -> new SimpleGrantedAuthority(Constants.ROLE_PREFIX + role))
            .collect(Collectors.toSet());
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        return Optional.ofNullable((Map<String, Object>) jwt.getClaim(REALM_ACCESS_CLAIM))
            .map(resource -> (List<String>) resource.get("roles"))
            .orElse(List.of())
            .stream()
            .map(role -> new SimpleGrantedAuthority(Constants.ROLE_PREFIX + role))
            .collect(Collectors.toSet());
    }
}
