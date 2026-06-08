package com.polzin.music.application.common;

public record AppError(String code, String message, Throwable cause) {

    public AppError {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }

    public static AppError of(String code, String message) {
        return new AppError(code, message, null);
    }
}
