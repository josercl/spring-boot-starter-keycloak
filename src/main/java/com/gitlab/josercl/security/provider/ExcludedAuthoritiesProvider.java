package com.gitlab.josercl.security.provider;

import com.gitlab.josercl.security.Constants;

import java.util.HashSet;
import java.util.Set;

public interface ExcludedAuthoritiesProvider {
    Set<String> DEFAULT_EXCLUDED_ROLES = Set.of(
        "default-roles-master",
        "offline_access",
        "uma_authorization",
        "manage-account",
        "manage-account-links",
        "view-profile"
    );

    Set<String> getExcludedAuthorities();

    default boolean accept(String authority) {
        return !getExcludedAuthorities().contains(authority);
    }

    static ExcludedAuthoritiesProvider.Builder builder() {
        return new Builder();
    }

    class Builder {
        private final Set<String> excludedAuthorities = new HashSet<>();

        public Builder excludeDefaultRoles() {
            DEFAULT_EXCLUDED_ROLES.forEach(this::excludeRole);
            return this;
        }

        public Builder excludeRole(String role) {
            excludedAuthorities.add(Constants.ROLE_PREFIX + role);
            return this;
        }

        public Builder excludeRoles(String... roles) {
            for (String role : roles) {
                excludeRole(role);
            }
            return this;
        }

        public Builder excludeScope(String scope) {
            excludedAuthorities.add(Constants.SCOPE_PREFIX + scope);
            return this;
        }

        public Builder excludeScopes(String... scopes) {
            for (String scope : scopes) {
                excludeScope(scope);
            }
            return this;
        }

        public ExcludedAuthoritiesProvider build() {
            return () -> excludedAuthorities;
        }
    }
}
