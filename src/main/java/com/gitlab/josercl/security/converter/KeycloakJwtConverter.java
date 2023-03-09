package com.gitlab.josercl.security.converter;

import com.gitlab.josercl.security.Constants;
import com.gitlab.josercl.security.properties.KeycloakProperties;
import com.gitlab.josercl.security.provider.ExcludedAuthoritiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@SuppressWarnings("unchecked")
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger log = LoggerFactory.getLogger(KeycloakJwtConverter.class);
    public static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    public static final String REALM_ACCESS_CLAIM = "realm_access";
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final KeycloakProperties properties;
    private final ExcludedAuthoritiesProvider excludedAuthoritiesProvider;

    public KeycloakJwtConverter(KeycloakProperties properties, ExcludedAuthoritiesProvider excludedAuthoritiesProvider) {
        this.properties = properties;
        this.excludedAuthoritiesProvider = excludedAuthoritiesProvider;

        log.info("Filtering out authorities: " + excludedAuthoritiesProvider.getExcludedAuthorities());
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> defaultJwtRoles = jwtGrantedAuthoritiesConverter.convert(jwt);
        Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
        Collection<GrantedAuthority> resourcesRoles = extractResourcesRoles(jwt);

        Set<GrantedAuthority> grantedAuthorities = Stream.concat(
                Stream.concat(defaultJwtRoles.stream(), realmRoles.stream()),
                resourcesRoles.stream()
            )
            .filter(grantedAuthority -> this.excludedAuthoritiesProvider.accept(grantedAuthority.getAuthority()))
            .collect(toSet());

        return new JwtAuthenticationToken(jwt, grantedAuthorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = Optional.ofNullable(properties.getAuth())
            .map(auth -> auth.getClient().getPrincipalAttribute())
            .orElse(JwtClaimNames.SUB);

        return jwt.getClaim(claimName);
    }

    private Collection<GrantedAuthority> extractResourcesRoles(Jwt jwt) {
        return Optional.ofNullable((Map<String, Object>) jwt.getClaim(RESOURCE_ACCESS_CLAIM))
            .map(resourceAccess -> (Map<String, Object>) resourceAccess.get(properties.getAuth().getClient().getClientId()))
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
