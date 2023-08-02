package com.gitlab.josercl.security;

import com.gitlab.josercl.security.converter.KeycloakJwtConverter;
import com.gitlab.josercl.security.mapper.DefaultRoleMapper;
import com.gitlab.josercl.security.mapper.RoleMapper;
import com.gitlab.josercl.security.properties.KeycloakProperties;
import com.gitlab.josercl.security.provider.ExcludedAuthoritiesProvider;
import com.gitlab.josercl.security.provider.PublicRoutesProvider;
import com.gitlab.josercl.security.provider.impl.DefaultAuthoritiesExcluder;
import com.gitlab.josercl.security.provider.impl.DefaultPublicRoutesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakSecurityAutoconfiguration {

    private static final Logger log = LoggerFactory.getLogger(KeycloakSecurityAutoconfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public ExcludedAuthoritiesProvider excludedAuthoritiesProvider() {
        return new DefaultAuthoritiesExcluder();
    }

    @Bean
    @ConditionalOnMissingBean
    public PublicRoutesProvider publicRoutesProvider() {
        return new DefaultPublicRoutesProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleMapper roleMapper(KeycloakProperties properties) {
        System.out.println("using default roleMapper");
        return new DefaultRoleMapper(properties.getAuth().getClient().getClientId());
    }

    @Bean(name = "keycloakJwtAuthConverter")
    @ConditionalOnProperty(prefix = "custom.config.keycloak", name = {"server", "realm", "auth.client.client-id"})
    public Converter<Jwt, AbstractAuthenticationToken> keycloakJwtAuthConverter(
        KeycloakProperties properties,
        RoleMapper roleMapper,
        ExcludedAuthoritiesProvider excludedAuthoritiesProvider
    ) {
        log.info("Using KeycloakJwtAuthConverter");
        return new KeycloakJwtConverter(properties, roleMapper, excludedAuthoritiesProvider);
    }

    @Bean
    @ConditionalOnMissingBean(name = "keycloakJwtAuthConverter")
    public Converter<Jwt, AbstractAuthenticationToken> defaultJwtAuthConverter() {
        log.info("Using defaultJwtAuthConverter");
        return new JwtAuthenticationConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        Converter<Jwt, AbstractAuthenticationToken> jwtConverter,
        PublicRoutesProvider publicRoutesProvider
    ) throws Exception {
        http
            .authorizeHttpRequests(authorizeConfig -> {

                List<String> publicRoutes1 = publicRoutesProvider.getPublicRoutes();
                log.info("Using public routes: {}", publicRoutes1);
                String[] publicRoutes = publicRoutes1.toArray(new String[0]);
                authorizeConfig.requestMatchers(publicRoutes).permitAll();

                authorizeConfig.anyRequest().authenticated();
            })
            .formLogin().disable()
            .csrf().disable()
            .oauth2ResourceServer(resourceServerCustomizer -> resourceServerCustomizer.jwt()
                .jwtAuthenticationConverter(jwtConverter)
            )
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
