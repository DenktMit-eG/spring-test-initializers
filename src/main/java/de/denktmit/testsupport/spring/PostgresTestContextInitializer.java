package de.denktmit.testsupport.spring;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * PostgresTestContextInitializer is an ApplicationContextInitializer for configuring a Spring Boot application context
 * for integration testing with a PostgreSQL database. It allows setting up database properties and using Flyway to
 * reset the database to a clean state.
 */
public class PostgresTestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * Default host is the database host used, if no environment variable is set for POSTGRES_HOST.
     */
    public static String DEFAULT_HOST = "localhost";

    /**
     * Default port is the database port used, if no environment variable is set for POSTGRES_PORT.
     */
    public static int DEFAULT_PORT = 35432;

    /**
     * Default database name used if no environment variable is set for POSTGRES_DB.
     */
    public static String DEFAULT_DB_NAME = "testsupport";

    /**
     * Default username used if no environment variable is set for POSTGRES_USER.
     */
    public static String DEFAULT_USERNAME = "db-it-user";

    /**
     * Default port is the database port used, if no environment variable is set for POSTGRES_PASSWORD.
     */
    public static String DEFAULT_PASSWORD = "db-it-pass";

    /**
     * Default behavior switch to adjust if Flyway shall execute the 'clean' action if no environment variable
     * is set for FLYWAY_CLEAN
     */
    public static boolean DEFAULT_FLYWAY_CLEAN = true;

    /**
     * Default behavior switch to adjust if Flyway shall execute the 'migrate' action if no environment variable
     * is set for FLYWAY_MIGRATE
     */
    public static boolean DEFAULT_FLYWAY_MIGRATE = true;

    /**
     * Configuration class holding values for Postgres setup. Provides default values
     * that can be overridden using environment variable .
     */
    public static class Config {
        String dbHost = System.getenv("POSTGRES_HOST") != null ? System.getenv("POSTGRES_HOST") : DEFAULT_HOST;
        int dbPort = System.getenv("POSTGRES_PORT") != null ? Integer.parseInt(System.getenv("POSTGRES_PORT")) : DEFAULT_PORT;
        String dbName = System.getenv("POSTGRES_DB") != null ? System.getenv("POSTGRES_DB") : DEFAULT_DB_NAME;
        String dbUser = System.getenv("POSTGRES_USER") != null ? System.getenv("POSTGRES_USER") : DEFAULT_USERNAME;
        String dbPassword = System.getenv("POSTGRES_PASSWORD") != null ? System.getenv("POSTGRES_PASSWORD") : DEFAULT_PASSWORD;
        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?loggerLevel=OFF";
        boolean flywayClean = System.getenv("FLYWAY_CLEAN") != null ? Boolean.parseBoolean(System.getenv("FLYWAY_CLEAN")) : DEFAULT_FLYWAY_CLEAN;
        boolean flywayMigrate = System.getenv("FLYWAY_MIGRATE") != null ? Boolean.parseBoolean(System.getenv("FLYWAY_MIGRATE")) : DEFAULT_FLYWAY_MIGRATE;
    }

    private final Config config = new Config();

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
        injectIntoSpringTestContext(configurableApplicationContext, config);
        resetDBWithFlyway(config);
    }

    private void injectIntoSpringTestContext(ConfigurableApplicationContext configurableApplicationContext, Config config) {
        TestPropertyValues.of(
                "spring.datasource.url=" + config.dbUrl,
                "spring.datasource.username=" + config.dbUser,
                "spring.datasource.password=" + config.dbPassword
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

    private void resetDBWithFlyway(Config ic) {
        ClassicConfiguration config = new ClassicConfiguration();
        config.setCleanDisabled(false);
        config.setLocationsAsStrings("");
        config.setDataSource(ic.dbUrl, ic.dbUser, ic.dbPassword);
        Flyway flyway = new Flyway(config);
        if (ic.flywayClean) {
            flyway.clean();
        }
        if (ic.flywayMigrate) {
            flyway.migrate();
        }
    }


    /**
     * Gets the resolved {@link Config} to be used by the initializer
     *
     * @return resolved {@link Config}
     */
    public Config getConfig() {
        return config;
    }
}
