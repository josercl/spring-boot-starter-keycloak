package com.gitlab.josercl.security.provider.impl;

import com.gitlab.josercl.security.Constants;
import com.gitlab.josercl.security.provider.ExcludedAuthoritiesProvider;

import java.util.Set;
import java.util.stream.Collectors;

public final class DefaultAuthoritiesExcluder implements ExcludedAuthoritiesProvider {

    @Override
    public Set<String> getExcludedAuthorities() {
        return DEFAULT_EXCLUDED_ROLES.stream()
            .map(s -> Constants.ROLE_PREFIX + s)
            .collect(Collectors.toSet());
    }
}
