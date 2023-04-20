package com.gitlab.josercl.security.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface PublicRoutesProvider {
    List<String> getPublicRoutes();

    static PublicRoutesProvider.Builder builder() {
        return new Builder();
    }

    final class Builder {
        private final List<String> publicRoutes = new ArrayList<>();

        private Builder() {}

        public Builder add(String route) {
            publicRoutes.add(route);
            return this;
        }

        public Builder withDefaults() {
             return withPrefix("/public");
        }

        public Builder withSwaggerUI() {
            return withSwaggerUI(builder -> {});
        }

        public Builder withSwaggerUI(Consumer<SwaggerRoutes.Builder> swaggerCustomizer) {
            SwaggerRoutes.Builder builder = SwaggerRoutes.builder();
            swaggerCustomizer.accept(builder);
            withSwaggerUI(builder);
            return this;
        }

        public Builder withSwaggerUI(SwaggerRoutes.Builder builder) {
            SwaggerRoutes build = builder.build();
            add(build.getSwaggerPath() + ".html");
            add(build.getSwaggerPath() + "/*");
            add(build.getSwaggerPath() + "/**");
            add(build.getApiDocsPath() + "/*");
            add(build.getApiDocsPath() + "/**");
            return this;
        }

        public Builder withPrefix(String prefix) {
            add(prefix);
            add(prefix + "/**");
            return this;
        }

        public Builder withActuator() {
            return withActuator("/actuator");
        }

        public Builder withActuator(String actuatorPrefix) {
            return withPrefix(actuatorPrefix);
        }

        public PublicRoutesProvider build() {
            return () -> publicRoutes;
        }
    }
}
