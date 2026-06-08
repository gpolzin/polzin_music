package com.polzin.music.infrastructure.db;

import com.polzin.music.infrastructure.logging.AppLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public final class DatabaseManager implements AutoCloseable {

    private static final Logger LOGGER = AppLogger.getLogger(DatabaseManager.class);
    private static final List<String> MIGRATIONS = List.of(
            "db/migrations/V001__core_schema.sql",
            "db/migrations/V002__providers_provenance.sql"
    );
        private static final Pattern STATEMENT_SPLITTER = Pattern.compile(";\\s*(?:\\R|$)");

    private final String jdbcUrl;

    public DatabaseManager(String jdbcUrl) {
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "jdbcUrl");
    }

    public Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        return connection;
    }

    public void initialize() throws SQLException {
        try (Connection connection = openConnection()) {
            ensureMigrationTable(connection);
            for (String migration : MIGRATIONS) {
                applyMigration(connection, migration);
            }
        }
    }

    private void applyMigration(Connection connection, String resourcePath) throws SQLException {
        String migrationVersion = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (isMigrationApplied(connection, migrationVersion)) {
            return;
        }
        String sql = loadResource(resourcePath);
        if (sql.isBlank()) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            for (String command : STATEMENT_SPLITTER.split(sql)) {
                String trimmed = command.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    statement.execute(trimmed);
                }
            }
        }
        markMigrationApplied(connection, migrationVersion);
        LOGGER.fine(() -> "Applied migration " + resourcePath);
    }

    private void ensureMigrationTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                        version TEXT PRIMARY KEY,
                        applied_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }
    }

    private boolean isMigrationApplied(Connection connection, String version) throws SQLException {
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT 1 FROM schema_migrations WHERE version = '" + version.replace("'", "''") + "'")) {
            return resultSet.next();
        }
    }

    private void markMigrationApplied(Connection connection, String version) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO schema_migrations(version) VALUES ('" + version.replace("'", "''") + "')");
        }
    }

    private String loadResource(String resourcePath) throws SQLException {
        ClassLoader classLoader = DatabaseManager.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new SQLException("Migration resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new SQLException("Failed to read migration resource: " + resourcePath, exception);
        }
    }

    @Override
    public void close() {
        // No pooled resources yet; each operation opens its own connection.
    }
}
