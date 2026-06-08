package com.polzin.music.infrastructure.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {

    private static volatile boolean configured;

    private AppLogger() {
    }

    public static Logger getLogger(Class<?> type) {
        configureIfNeeded();
        return Logger.getLogger(type.getName());
    }

    private static void configureIfNeeded() {
        if (configured) {
            return;
        }
        synchronized (AppLogger.class) {
            if (configured) {
                return;
            }
            Logger root = Logger.getLogger("");
            for (var handler : root.getHandlers()) {
                root.removeHandler(handler);
            }
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.INFO);
            root.addHandler(handler);
            root.setLevel(Level.INFO);
            configured = true;
        }
    }
}
