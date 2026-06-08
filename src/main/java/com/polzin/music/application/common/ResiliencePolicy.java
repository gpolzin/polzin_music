package com.polzin.music.application.common;

import java.io.IOException;
import java.util.Objects;

public final class ResiliencePolicy {

    public enum FailureDomain {
        PROVIDER,
        FILESYSTEM
    }

    public static final class ResilienceFailure extends RuntimeException {

        private final FailureDomain failureDomain;
        private final String operation;
        private final boolean retryable;

        public ResilienceFailure(FailureDomain failureDomain,
                                 String operation,
                                 String message,
                                 boolean retryable,
                                 Throwable cause) {
            super(message, cause);
            this.failureDomain = Objects.requireNonNull(failureDomain, "failureDomain");
            this.operation = Objects.requireNonNull(operation, "operation");
            this.retryable = retryable;
        }

        public FailureDomain failureDomain() {
            return failureDomain;
        }

        public String operation() {
            return operation;
        }

        public boolean retryable() {
            return retryable;
        }
    }

    @FunctionalInterface
    public interface CheckedSupplier<T> {

        T get() throws Exception;
    }

    @FunctionalInterface
    public interface CheckedRunnable {

        void run() throws Exception;
    }

    public <T> T protectProvider(String operation, CheckedSupplier<T> action) {
        return execute(operation, FailureDomain.PROVIDER, true, action);
    }

    public <T> T protectFilesystem(String operation, CheckedSupplier<T> action) {
        return execute(operation, FailureDomain.FILESYSTEM, false, action);
    }

    public void protectFilesystem(String operation, CheckedRunnable action) {
        execute(operation, FailureDomain.FILESYSTEM, false, () -> {
            action.run();
            return null;
        });
    }

    private <T> T execute(String operation,
                          FailureDomain failureDomain,
                          boolean retryable,
                          CheckedSupplier<T> action) {
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(action, "action");
        try {
            return action.get();
        } catch (Exception exception) {
            throw new ResilienceFailure(
                    failureDomain,
                    operation,
                    buildMessage(operation, failureDomain, exception),
                    retryable,
                    exception
            );
        }
    }

    private String buildMessage(String operation, FailureDomain failureDomain, Exception exception) {
        String source = switch (failureDomain) {
            case PROVIDER -> "provider";
            case FILESYSTEM -> "filesystem";
        };
        String type = exception instanceof IOException ? "I/O" : exception.getClass().getSimpleName();
        return source + " failure during " + operation + " (" + type + ")";
    }
}
