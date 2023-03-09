package com.gitlab.josercl.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.config.keycloak")
public class KeycloakProperties {

    private String server;
    private String realm;
    private Auth auth;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {
        private AuthClient client;

        public AuthClient getClient() {
            return client;
        }

        public void setClient(AuthClient client) {
            this.client = client;
        }
    }

    public static class AuthClient {
        private String clientId;
        private String principalAttribute;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getPrincipalAttribute() {
            return principalAttribute;
        }

        public void setPrincipalAttribute(String principalAttribute) {
            this.principalAttribute = principalAttribute;
        }
    }
}
