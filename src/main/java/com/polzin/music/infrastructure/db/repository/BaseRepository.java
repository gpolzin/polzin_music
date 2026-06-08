package com.polzin.music.infrastructure.db.repository;

import com.polzin.music.infrastructure.db.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
interface SqlFunction<T, R> {
    R apply(T value) throws SQLException;
}

@FunctionalInterface
interface SqlConsumer<T> {
    void accept(T value) throws SQLException;
}

public abstract class BaseRepository {

    private final DatabaseManager databaseManager;

    protected BaseRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    protected final <T> T withConnection(SqlFunction<Connection, T> action) throws SQLException {
        try (Connection connection = databaseManager.openConnection()) {
            return action.apply(connection);
        }
    }

    protected final void withTransaction(SqlConsumer<Connection> action) throws SQLException {
        withConnection(connection -> {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                action.accept(connection);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
            return null;
        });
    }
}
