package de.denktmit.testsupport.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresTestContextInitializerIT {

    private PostgresTestContextInitializer initializer;
    private ConfigurableApplicationContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        initializer = new PostgresTestContextInitializer();
        ctx = new GenericApplicationContext();
        dropAllTablesIfExistent();
    }

    void dropAllTablesIfExistent() throws Exception {
        String jdbcUrl = initializer.getIc().dbUrl;
        String username = initializer.getIc().dbUser;
        String password = initializer.getIc().dbPassword;
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS example_table");
            statement.execute("DROP TABLE IF EXISTS flyway_schema_history");
        }
    }

    @Test
    void testInitialize() throws Exception {
        PostgresTestContextInitializer initializer = new PostgresTestContextInitializer();
        initializer.initialize(ctx);
        validateSpringIntegration();
        validateDataInExampleTable();
    }

    private void validateSpringIntegration() {
        PropertySource<?> testPropertySource = ctx.getEnvironment().getPropertySources().get("test");
        assertThat(testPropertySource).isNotNull();
        assertThat(testPropertySource.getProperty("spring.datasource.url")).isEqualTo(initializer.getIc().dbUrl);
        assertThat(testPropertySource.getProperty("spring.datasource.username")).isEqualTo(initializer.getIc().dbUser);
        assertThat(testPropertySource.getProperty("spring.datasource.password")).isEqualTo(initializer.getIc().dbPassword);
    }

    void validateDataInExampleTable() throws Exception {
        // Database connection information
        String jdbcUrl = initializer.getIc().dbUrl;
        String username = initializer.getIc().dbUser;
        String password = initializer.getIc().dbPassword;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, name FROM example_table")) {

            // Transform the ResultSet into a Map<Integer, String>
            Map<Integer, String> dataMap = new HashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                dataMap.put(id, name);
            }

            // Validate the data in the Map
            assertThat(dataMap).hasSize(4);
            assertThat(dataMap).containsEntry(1, "John");
            assertThat(dataMap).containsEntry(2, "Alice");
            assertThat(dataMap).containsEntry(3, "Bob");
            assertThat(dataMap).containsEntry(4, "Eve");
        }
    }
}
