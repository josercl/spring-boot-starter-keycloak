package com.gitlab.josercl.security.mapper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.function.Function;

public interface ScopeMapperExtender extends Function<Jwt, Collection<GrantedAuthority>> {

}
