package com.gitlab.josercl.security;

import com.gitlab.josercl.security.converter.KeycloakJwtConverter;
import com.gitlab.josercl.security.properties.KeycloakProperties;
import com.gitlab.josercl.security.provider.PublicRoutesProvider;
import com.gitlab.josercl.security.provider.impl.DefaultPublicRoutesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Import({KeycloakProperties.class})
@EnableWebSecurity
@EnableMethodSecurity
public class KeycloakSecurityAutoconfiguration {

    private static final Logger log = LoggerFactory.getLogger(KeycloakSecurityAutoconfiguration.class);

    @Bean
    @Order
    @ConditionalOnBean(KeycloakProperties.class)
    public Converter<Jwt, AbstractAuthenticationToken> keycloakJwtAuthConverter(KeycloakProperties properties) {
        log.info("Using KeycloakJwtAuthConverter");
        return new KeycloakJwtConverter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public Converter<Jwt, AbstractAuthenticationToken> defaultJwtAuthConverter() {
        log.info("Using defaultJwtAuthConverter");
        return new JwtAuthenticationConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public PublicRoutesProvider publicRoutesProvider() {
        return new DefaultPublicRoutesProvider();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        Converter<Jwt, AbstractAuthenticationToken> jwtConverter,
        PublicRoutesProvider publicRoutesProvider
    ) throws Exception {
        http
            .authorizeHttpRequests(authorizeConfig -> {

                String[] publicRoutes = publicRoutesProvider.getPublicRoutes().toArray(new String[0]);
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
