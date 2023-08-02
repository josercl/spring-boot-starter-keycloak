package com.gitlab.josercl.security.converter;

import com.gitlab.josercl.security.mapper.RoleMapper;
import com.gitlab.josercl.security.properties.KeycloakProperties;
import com.gitlab.josercl.security.provider.ExcludedAuthoritiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger log = LoggerFactory.getLogger(KeycloakJwtConverter.class);
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final KeycloakProperties properties;
    private final RoleMapper roleMapper;
    private final ExcludedAuthoritiesProvider excludedAuthoritiesProvider;

    public KeycloakJwtConverter(
        KeycloakProperties properties,
        RoleMapper roleMapper,
        ExcludedAuthoritiesProvider excludedAuthoritiesProvider
    ) {
        this.properties = properties;
        this.roleMapper = roleMapper;
        this.excludedAuthoritiesProvider = excludedAuthoritiesProvider;

        log.info("Filtering out authorities: " + excludedAuthoritiesProvider.getExcludedAuthorities());
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> defaultJwtRoles = jwtGrantedAuthoritiesConverter.convert(jwt);
        Collection<GrantedAuthority> mapperRoles = roleMapper.mapRoles(jwt);

        Set<GrantedAuthority> grantedAuthorities = Stream.concat(
                defaultJwtRoles.stream(),
                mapperRoles.stream()
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
}
