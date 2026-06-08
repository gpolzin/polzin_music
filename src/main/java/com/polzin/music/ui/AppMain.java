package com.polzin.music.ui;

import com.polzin.music.infrastructure.config.AppConfig;

public final class AppMain {

    private AppMain() {
    }

    public static void main(String[] args) {
        AppConfig config = AppConfig.fromEnvironment();
        System.out.println("Polzin Music bootstrap");
        System.out.println("Source: " + config.sourceRoot());
        System.out.println("Destination: " + config.destinationRoot());
        System.out.println("DB: " + config.sqliteUrl());
    }
}
