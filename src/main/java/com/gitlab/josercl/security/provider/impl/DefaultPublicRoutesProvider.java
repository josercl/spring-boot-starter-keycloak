package com.gitlab.josercl.security.provider.impl;


import com.gitlab.josercl.security.provider.PublicRoutesProvider;

import java.util.List;

public class DefaultPublicRoutesProvider implements PublicRoutesProvider {
    @Override
    public List<String> getPublicRoutes() {
        return List.of(
            "/public/**"
        );
    }
}
