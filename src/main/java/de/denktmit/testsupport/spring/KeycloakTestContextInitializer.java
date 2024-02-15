package de.denktmit.testsupport.spring;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * {@code KeycloakTestContextInitializer} is an implementation of the
 * {@link ApplicationContextInitializer} interface for setting up Keycloak configuration
 * during integration tests. It initializes a Keycloak test environment, creating realms,
 * clients, users, and roles necessary for testing with Keycloak.
 * <p>
 * This class provides default configuration values that can be overridden using
 * environment variable . It also exposes an {@link Config} class to customize
 * the Keycloak configuration.
 *
 * @author [Your Name]
 * @version 1.0
 * @see ApplicationContextInitializer
 */
public class KeycloakTestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private Config config = new Config();

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        setupKeycloak(config);
        TestPropertyValues.of(
                "spring.security.oauth2.client.provider.keycloak.issuer-uri=http://" + config.keycloakAddress + "/auth/realms/" + config.testRealmName,
                "spring.security.oauth2.client.registration.keycloak.client-id=" + config.testClientId,
                "spring.security.oauth2.client.registration.keycloak.client-secret=" + config.testClientSecret
        ).applyTo(applicationContext.getEnvironment());
    }

    private void setupKeycloak(Config ic) {
        KeycloakSession keycloakSession = new KeycloakSession(ic);
        keycloakSession.connectAdminClient();
        keycloakSession.createSandboxRealm();
        keycloakSession.createSandboxRealmClient();
        keycloakSession.setupNewUser();
    }

    /**
     * Gets the resolved {@link Config} to be used by the initializer
     *
     * @return resolved {@link Config}
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Configuration class holding values for Keycloak setup. Provides default values
     * that can be overridden using environment variable .
     */
    public static class Config {
        /**
         * Default schema for Keycloak. Defaults to "http".
         */
        public static String URI_SCHEMA = "http";

        /**
         * Default host for Keycloak. Defaults to "localhost".
         */
        public static String DEFAULT_HOST = "localhost";

        /**
         * Default port for Keycloak. Defaults to 18080.
         */
        public static int DEFAULT_PORT = 18080;

        /**
         * Default administrator username for Keycloak. Defaults to "admin".
         */
        public static String DEFAULT_ADMIN_USERNAME = "admin";

        /**
         * Default administrator password for Keycloak. Defaults to "admin".
         */
        public static String DEFAULT_ADMIN_PASSWORD = "admin";

        /**
         * Default master realm for Keycloak. Defaults to "master".
         */
        public static String DEFAULT_MASTER_REALM = "master";

        /**
         * Default client ID for the Keycloak admin client. Defaults to "admin-cli".
         */
        public static String DEFAULT_ADMIN_CLIENT_ID = "admin-cli";

        /**
         * Default client ID for the Keycloak test client. Defaults to "sb-client".
         */
        public static String TEST_CLIENT_ID = "sb-client";

        /**
         * Default client secret for the Keycloak test client.
         */
        public static String TEST_CLIENT_SECRET = "ad0be000-0000-4000-a000-000000000000";

        /**
         * Default realm name for the Keycloak test environment. Defaults to "sandbox".
         */
        public static String TEST_REALM_NAME = "sandbox";

        /**
         * Default user role for the Keycloak test user. Defaults to "sb-manager".
         */
        public static String TEST_USER_ROLE = "sb-manager";

        /**
         * Default description for the Keycloak test user role. Defaults to "sandbox manager".
         */
        public static String TEST_USER_ROLE_DESCRIPTION = "sandbox manager";

        /**
         * Default username for the Keycloak test admin user. Defaults to "sb-admin".
         */
        public static String TEST_ADMIN_USERNAME = "sb-admin";

        /**
         * Default password for the Keycloak test admin user. Defaults to "ThisIsHow2ConnectAnAdmin!".
         */
        public static String TEST_ADMIN_PASSWORD = "ThisIsHow2ConnectAnAdmin!";

        private String keycloakUriSchema = System.getenv("KEYCLOAK_HOST_URI_SCHEMA") != null ? System.getenv("KEYCLOAK_HOST_URI_SCHEMA") : URI_SCHEMA;
        private String keycloakHost = System.getenv("KEYCLOAK_HOST") != null ? System.getenv("KEYCLOAK_HOST") : DEFAULT_HOST;
        private int keycloakPort = System.getenv("KEYCLOAK_PORT") != null ? Integer.parseInt(System.getenv("KEYCLOAK_PORT")) : DEFAULT_PORT;
        private String keycloakAdminName = System.getenv("KEYCLOAK_USER") != null ? System.getenv("KEYCLOAK_USER") : DEFAULT_ADMIN_USERNAME;
        private String keycloakAdminPassword = System.getenv("KEYCLOAK_PASSWORD") != null ? System.getenv("KEYCLOAK_PASSWORD") : DEFAULT_ADMIN_PASSWORD;
        private String keycloakAddress =  keycloakUriSchema + "://" + keycloakHost + ":" + keycloakPort;
        private String keycloakMasterRealm = System.getenv("KEYCLOAK_MASTER_REALM") != null ? System.getenv("KEYCLOAK_MASTER_REALM") : DEFAULT_MASTER_REALM;
        private String keycloakAdminClientId = System.getenv("KEYCLOAK_ADMIN_CLIENT_ID") != null ? System.getenv("KEYCLOAK_ADMIN_CLIENT_ID") : DEFAULT_ADMIN_CLIENT_ID;
        private String testClientId = System.getenv("KEYCLOAK_TEST_CLIENT_ID") != null ? System.getenv("KEYCLOAK_TEST_CLIENT_ID") : TEST_CLIENT_ID;
        private String testClientSecret = System.getenv("KEYCLOAK_TEST_CLIENT_SECRET") != null ? System.getenv("KEYCLOAK_TEST_CLIENT_SECRET") : TEST_CLIENT_SECRET;
        private String testRealmName = System.getenv("KEYCLOAK_TEST_REALM_NAME") != null ? System.getenv("KEYCLOAK_TEST_REALM_NAME") : TEST_REALM_NAME;
        private String testUserRole = System.getenv("KEYCLOAK_TEST_USER_ROLE") != null ? System.getenv("KEYCLOAK_TEST_USER_ROLE") : TEST_USER_ROLE;
        private String testUserRoleDescription = System.getenv("KEYCLOAK_TEST_USER_ROLE_DESCRIPTION") != null ? System.getenv("KEYCLOAK_TEST_USER_ROLE_DESCRIPTION") : TEST_USER_ROLE_DESCRIPTION;
        private String testAdminUsername = System.getenv("KEYCLOAK_TEST_ADMIN_USERNAME") != null ? System.getenv("KEYCLOAK_TEST_ADMIN_USERNAME") : TEST_ADMIN_USERNAME;
        private String testAdminPassword = System.getenv("KEYCLOAK_TEST_ADMIN_PASSWORD") != null ? System.getenv("KEYCLOAK_TEST_ADMIN_PASSWORD") : TEST_ADMIN_PASSWORD;

        /**
         * Get the Keycloak uri schema. Defaults to {@link #URI_SCHEMA} if not provided through environment variable KEYCLOAK_HOST_URI_SCHEMA.
         *
         * @return The Keycloak host.
         */
        public String getKeycloakUriSchema() {
            return keycloakUriSchema;
        }

        /**
         * Get the Keycloak host. Defaults to {@link #DEFAULT_HOST} if not provided through environment variable KEYCLOAK_HOST.
         *
         * @return The Keycloak host.
         */
        public String getKeycloakHost() {
            return keycloakHost;
        }

        /**
         * Get the Keycloak port. Defaults to {@link #DEFAULT_PORT} if not provided through environment variable KEYCLOAK_PORT.
         *
         * @return The Keycloak port.
         */
        public int getKeycloakPort() {
            return keycloakPort;
        }

        /**
         * Get the administrator username for Keycloak. Defaults to {@link #DEFAULT_ADMIN_USERNAME} if not provided through environment variable KEYCLOAK_USER.
         *
         * @return The Keycloak administrator username.
         */
        public String getKeycloakAdminName() {
            return keycloakAdminName;
        }

        /**
         * Get the administrator password for Keycloak. Defaults to {@link #DEFAULT_ADMIN_PASSWORD} if not provided through environment variable KEYCLOAK_PASSWORD.
         *
         * @return The Keycloak administrator password.
         */
        public String getKeycloakAdminPassword() {
            return keycloakAdminPassword;
        }

        /**
         * Get the Keycloak server address in the format "host:port". Defaults to a combination
         * of {@code keycloakHost} and {@code keycloakPort} separated by ':'.
         *
         * @return The Keycloak server address.
         */
        public String getKeycloakAddress() {
            return keycloakAddress;
        }

        /**
         * Get the master realm for Keycloak. Defaults to {@link #DEFAULT_MASTER_REALM} if not provided through environment variable KEYCLOAK_MASTER_REALM.
         *
         * @return The Keycloak master realm.
         */
        public String getKeycloakMasterRealm() {
            return keycloakMasterRealm;
        }

        /**
         * Get the client ID for the Keycloak admin client. Defaults to {@link #DEFAULT_ADMIN_CLIENT_ID} if not provided through environment variable KEYCLOAK_ADMIN_CLIENT_ID.
         *
         * @return The Keycloak admin client ID.
         */
        public String getKeycloakAdminClientId() {
            return keycloakAdminClientId;
        }

        /**
         * Get the client ID for the Keycloak test client. Defaults to {@link #TEST_CLIENT_ID} if not provided through environment variable KEYCLOAK_TEST_CLIENT_ID.
         *
         * @return The Keycloak test client ID.
         */
        public String getTestClientId() {
            return testClientId;
        }

        /**
         * Get the client secret for the Keycloak test client. Defaults to {@link #TEST_CLIENT_SECRET} if not provided through environment variable KEYCLOAK_TEST_CLIENT_SECRET.
         *
         * @return The Keycloak test client secret.
         */
        public String getTestClientSecret() {
            return testClientSecret;
        }

        /**
         * Get the realm name for the Keycloak test environment. Defaults to {@link #TEST_REALM_NAME} if not provided through environment variable KEYCLOAK_TEST_REALM_NAME.
         *
         * @return The Keycloak test realm name.
         */
        public String getTestRealmName() {
            return testRealmName;
        }

        /**
         * Get the user role for the Keycloak test user. Defaults to {@link #TEST_USER_ROLE} if not provided through environment variable KEYCLOAK_TEST_USER_ROLE.
         *
         * @return The Keycloak test user role.
         */
        public String getTestUserRole() {
            return testUserRole;
        }

        /**
         * Get the description for the Keycloak test user role. Defaults to {@link  #TEST_USER_ROLE_DESCRIPTION} if not provided through environment variable KEYCLOAK_TEST_USER_ROLE_DESCRIPTION.
         *
         * @return The Keycloak test user role description.
         */
        public String getTestUserRoleDescription() {
            return testUserRoleDescription;
        }

        /**
         * Get the username for the Keycloak test admin user. Defaults to {@link #TEST_ADMIN_USERNAME} if not provided through environment variable KEYCLOAK_TEST_ADMIN_USERNAME.
         *
         * @return The Keycloak test admin username.
         */
        public String getTestAdminUsername() {
            return testAdminUsername;
        }

        /**
         * Get the password for the Keycloak test admin user. Defaults to {@link #TEST_ADMIN_PASSWORD} if not provided through environment variable KEYCLOAK_TEST_ADMIN_PASSWORD.
         *
         * @return The Keycloak test admin password.
         */
        public String getTestAdminPassword() {
            return testAdminPassword;
        }
    }

    private static class KeycloakSession {
        private final Config ic;
        private Keycloak keycloakAdminClient;
        private RealmResource realm;
        private UsersResource users;

        public KeycloakSession(Config ic) {
            this.ic = ic;
        }

        private void connectAdminClient() {
            keycloakAdminClient = KeycloakBuilder.builder()
                    .serverUrl(ic.keycloakAddress + "/auth")
                    .realm(ic.keycloakMasterRealm)
                    .clientId(ic.keycloakAdminClientId)
                    .username(ic.keycloakAdminName)
                    .password(ic.keycloakAdminPassword)
                    .build();
        }

        private void createSandboxRealm() {
            deleteRealmIfExists();
            keycloakAdminClient.realms().create(
                    new RealmRepresentation() {{
                        setRealm(ic.testRealmName);
                        setEnabled(true);
                    }}
            );
            realm = keycloakAdminClient.realm(ic.testRealmName);
            users = realm.users();
        }

        private void deleteRealmIfExists() {
            keycloakAdminClient.realms().findAll().stream()
                    .filter(r -> r.getRealm().equals(ic.testRealmName))
                    .findFirst()
                    .ifPresent(r -> keycloakAdminClient.realm(ic.testRealmName).remove());
        }

        private void createSandboxRealmClient() {
            realm.clients().create(
                    new ClientRepresentation() {{
                        setId(ic.testClientId);
                        setSecret(ic.testClientSecret);
                        setRedirectUris(List.of("*"));
                        setWebOrigins(List.of("*"));
                        setProtocol("openid-connect");
                    }}
            );
        }

        private void setupNewUser() {
            deleteUserIfExists();
            Response userCreateResponse = createUser();
            UserResource userResource = users.get(CreatedResponseUtil.getCreatedId(userCreateResponse));
            setUserPassword(userResource);
            createRole(userResource);
        }

        private void deleteUserIfExists() {
            users.list().stream()
                    .filter(user -> user.getUsername().equals(ic.testAdminUsername))
                    .findFirst()
                    .ifPresent(user -> users.delete(user.getId()));
        }

        private Response createUser() {
            return users.create(
                    new UserRepresentation() {{
                        setEnabled(true);
                        setUsername(ic.testAdminUsername);
                        setFirstName("JUnit");
                        setLastName("Tester");
                        setEmail("junit+tester1@gec.io");
                        setAttributes(java.util.Collections.singletonMap("origin", List.of("demo")));
                    }}
            );
        }

        private void setUserPassword(UserResource userResource) {
            userResource.resetPassword(
                    new CredentialRepresentation() {{
                        setTemporary(false);
                        setType(CredentialRepresentation.PASSWORD);
                        setValue(ic.testAdminPassword);
                    }}
            );
        }

        private void createRole(UserResource... assignees) {
            deleteRoleIfExists();
            realm.roles().create(new RoleRepresentation(ic.testUserRole, ic.testUserRoleDescription, false));
            RoleRepresentation role = realm.roles().get(ic.testUserRole).toRepresentation();
            for (UserResource user : assignees) {
                user.roles().realmLevel().add(Collections.singletonList(role));
            }
        }

        private void deleteRoleIfExists() {
            realm.roles().list().stream()
                    .filter(r -> r.getName().equals(ic.testUserRole))
                    .findFirst()
                    .ifPresent(r -> realm.roles().deleteRole(r.getName()));
        }
    }

}
