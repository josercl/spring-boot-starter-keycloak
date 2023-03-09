Agrega Repositorio en build.gradle

```groovy
repositories {
    maven {
        url 'https://gitlab.com/api/v4/projects/44022756/packages/maven'
    }
}
```

Agregar dependencia

```groovy
implementation 'com.gitlab.josercl:spring-boot-starter-keycloak:1.0.1'
```

Editar application.properties (application.yml) y agregar las siguientes propiedades:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${custom.config.keycloak.server:http://localhost:8080}/realms/${custom.config.keycloak.realm:dummy}
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs

custom.config.keycloak.server=${KEYCLOAK_SERVER:http://localhost}
custom.config.keycloak.realm=${KEYCLOAK_REALM:realm}

custom.config.keycloak.auth.client.client-id=${KEYCLOAK_CLIENT_ID:client-id}
custom.config.keycloak.auth.client.principal-attribute=preferred_username
```

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${custom.config.keycloak.server:http://localhost:8080}/realms/${custom.config.keycloak.realm:dummy}
          jwk-set-uri: ${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs

custom:
  config:
    keycloak:
      server: ${KEYCLOAK_SERVER:http://localhost}
      realm: ${KEYCLOAK_REALM:realm}
      auth:
        client:
          client-id: ${KEYCLOAK_CLIENT_ID:client-id}
          principal-attribute: preferred_username
```