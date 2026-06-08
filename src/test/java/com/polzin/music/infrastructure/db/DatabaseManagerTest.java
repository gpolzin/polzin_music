package com.polzin.music.infrastructure.db;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {

    @Test
    void appliesMigrationsAndCreatesCoreTables() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-music-test", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            try (Connection connection = databaseManager.openConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='artists'")) {
                assertTrue(resultSet.next());
            }
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}
