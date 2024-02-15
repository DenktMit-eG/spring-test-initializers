package de.denktmit.testsupport.spring;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresTestContextInitializerTest {

    @Test
    @SetEnvironmentVariable(key = "POSTGRES_HOST", value = "db.example.com")
    @SetEnvironmentVariable(key = "POSTGRES_PORT", value = "5432")
    @SetEnvironmentVariable(key = "POSTGRES_DB", value = "prod")
    @SetEnvironmentVariable(key = "POSTGRES_USER", value = "db-test-user")
    @SetEnvironmentVariable(key = "POSTGRES_PASSWORD", value = "db-test-password")
    @SetEnvironmentVariable(key = "FLYWAY_CLEAN", value = "false")
    @SetEnvironmentVariable(key = "FLYWAY_MIGRATE", value = "false")
    void testEnvironmentVariablePickup() throws Exception {
        PostgresTestContextInitializer initializer = new PostgresTestContextInitializer();
//                withEnvironmentVariable("first", "first value")
//                        .and("POSTGRES_HOST", "db.example.com")
//                        .and("POSTGRES_PORT", "5432")
//                        .and("POSTGRES_DB", "prod")
//                        .and("POSTGRES_USER", "db-test-user")
//                        .and("POSTGRES_PASSWORD", "db-test-password")
//                        .and("FLYWAY_CLEAN", "true")
//                        .and("FLYWAY_MIGRATE", "false")
//                        .execute(PostgresTestContextInitializer::new);


        initializer.initialize(new GenericApplicationContext());

        // Verify the properties of the initializerConfig using AssertJ
        assertThat(initializer.getConfig().dbHost).isEqualTo("db.example.com");
        assertThat(initializer.getConfig().dbPort).isEqualTo(5432);
        assertThat(initializer.getConfig().dbName).isEqualTo("prod");
        assertThat(initializer.getConfig().dbUser).isEqualTo("db-test-user");
        assertThat(initializer.getConfig().dbPassword).isEqualTo("db-test-password");
        assertThat(initializer.getConfig().flywayClean).isFalse();
        assertThat(initializer.getConfig().flywayMigrate).isFalse();

    }

}
