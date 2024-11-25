package com.gitlab.josercl.security.provider.impl;

import com.gitlab.josercl.security.provider.ExcludedAuthoritiesProvider;

import java.util.Collections;
import java.util.Set;

@SuppressWarnings("unused")
public class EmptyExcludedAuthoritiesProvider implements ExcludedAuthoritiesProvider {
    @Override
    public Set<String> getExcludedAuthorities() {
        return Collections.emptySet();
    }
}
