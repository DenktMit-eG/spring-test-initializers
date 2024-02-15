package de.denktmit.testsupport.spring;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

public class KeycloakTestContextInitializerTest {

    @Test
    @SetEnvironmentVariable(key = "KEYCLOAK_HOST_URI_SCHEMA", value = "https")
    @SetEnvironmentVariable(key = "KEYCLOAK_HOST", value = "keycloak.dangerzone.denktmit.tech")
    @SetEnvironmentVariable(key = "KEYCLOAK_PORT", value = "28080")
    @SetEnvironmentVariable(key = "KEYCLOAK_USER", value = "superuser")
    @SetEnvironmentVariable(key = "KEYCLOAK_PASSWORD", value = "superpassword")
    @SetEnvironmentVariable(key = "KEYCLOAK_MASTER_REALM", value = "main")
    @SetEnvironmentVariable(key = "KEYCLOAK_ADMIN_CLIENT_ID", value = "superuser-cli")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_CLIENT_ID", value = "junit-client")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_CLIENT_SECRET", value = "decade00-0000-4000-a000-000000000000")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_REALM_NAME", value = "playground")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_USER_ROLE", value = "playground-manager")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_USER_ROLE_DESCRIPTION", value = "playground manager")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_ADMIN_USERNAME", value = "playground-admin")
    @SetEnvironmentVariable(key = "KEYCLOAK_TEST_ADMIN_PASSWORD", value = "ThisIsHow2ConnectAnPlaygroundAdmin!")
    void testEnvironmentVariablePickup() throws Exception {
        KeycloakTestContextInitializer initializer = new KeycloakTestContextInitializer();

        // Verify the properties of the initializerConfig using AssertJ
        KeycloakTestContextInitializer.Config ic = initializer.getConfig();
        assertThat(ic.getKeycloakUriSchema()).isEqualTo("https");
        assertThat(ic.getKeycloakHost()).isEqualTo("keycloak.dangerzone.denktmit.tech");
        assertThat(ic.getKeycloakPort()).isEqualTo(28080);
        assertThat(ic.getKeycloakAddress()).isEqualTo("https://keycloak.dangerzone.denktmit.tech:28080");
        assertThat(ic.getKeycloakAdminName()).isEqualTo("superuser");
        assertThat(ic.getKeycloakAdminPassword()).isEqualTo("superpassword");
        assertThat(ic.getKeycloakMasterRealm()).isEqualTo("main");
        assertThat(ic.getKeycloakAdminClientId()).isEqualTo("superuser-cli");
        assertThat(ic.getTestClientId()).isEqualTo("junit-client");
        assertThat(ic.getTestClientSecret()).isEqualTo("decade00-0000-4000-a000-000000000000");
        assertThat(ic.getTestRealmName()).isEqualTo("playground");
        assertThat(ic.getTestUserRole()).isEqualTo("playground-manager");
        assertThat(ic.getTestUserRoleDescription()).isEqualTo("playground manager");
        assertThat(ic.getTestAdminUsername()).isEqualTo("playground-admin");
        assertThat(ic.getTestAdminPassword()).isEqualTo("ThisIsHow2ConnectAnPlaygroundAdmin!");
    }

}
