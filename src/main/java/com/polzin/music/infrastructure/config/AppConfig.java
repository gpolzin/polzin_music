package com.polzin.music.infrastructure.config;

import java.nio.file.Path;

public record AppConfig(Path sourceRoot, Path destinationRoot, String sqliteUrl) {

    public static AppConfig fromEnvironment() {
        Path source = Path.of(System.getProperty("polzin.sourceRoot", "./input"));
        Path destination = Path.of(System.getProperty("polzin.destinationRoot", "./organized"));
        String dbUrl = System.getProperty("polzin.sqliteUrl", "jdbc:sqlite:polzin_music.db");
        return new AppConfig(source, destination, dbUrl);
    }
}
