package com.gitlab.josercl.security.provider;

import com.gitlab.josercl.security.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface PublicRoutesProvider {
    List<Pair<String, HttpMethod>> getPublicRoutes();

    static PublicRoutesProvider.Builder builder() {
        return new Builder();
    }

    final class Builder {
        private final List<Pair<String, HttpMethod>> publicRoutesWithMethods = new ArrayList<>();

        private Builder() {}

        public Builder add(String route, HttpMethod method) {
            publicRoutesWithMethods.add(new Pair<>(route, method));
            return this;
        }

        public Builder add(String route) {
            add(route, null);
            return this;
        }

        public Builder withDefaults() {
             return withPrefix("/public");
        }

        public Builder withSwaggerUI() {
            return withSwaggerUI(builder -> {});
        }

        public Builder withSwaggerUI(Customizer<SwaggerRoutes.Builder> swaggerCustomizer) {
            SwaggerRoutes.Builder builder = SwaggerRoutes.builder();
            swaggerCustomizer.customize(builder);
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

        public Builder customizeWith(Customizer<Builder> customizer) {
            Optional.ofNullable(customizer).ifPresent(c -> c.customize(this));
            return this;
        }

        public PublicRoutesProvider build() {
            return () -> publicRoutesWithMethods;
        }
    }
}
