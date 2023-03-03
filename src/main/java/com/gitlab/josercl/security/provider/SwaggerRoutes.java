package com.gitlab.josercl.security.provider;

public class SwaggerRoutes {
    public static final String DEFAULT_SWAGGER_PATH = "/swagger-ui";
    public static final String DEFAULT_API_DOCS_PATH = "/v3/api-docs";
    private final String swaggerPath;
    private final String apiDocsPath;
    private SwaggerRoutes(String swaggerPath, String apiDocsPath){
        this.swaggerPath = swaggerPath;
        this.apiDocsPath = apiDocsPath;
    }

    public String getSwaggerPath() {
        return swaggerPath;
    }

    public String getApiDocsPath() {
        return apiDocsPath;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String swaggerPath = DEFAULT_SWAGGER_PATH;
        private String apiDocsPath = DEFAULT_API_DOCS_PATH;

        private Builder(){}

        SwaggerRoutes build() {
            return new SwaggerRoutes(swaggerPath, apiDocsPath);
        }

        public Builder withPath(String path) {
            this.swaggerPath = path;
            return this;
        }

        public Builder withApiDocs(String path) {
            this.apiDocsPath = path;
            return this;
        }
    }
}