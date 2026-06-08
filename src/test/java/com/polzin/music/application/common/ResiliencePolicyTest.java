package com.polzin.music.application.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResiliencePolicyTest {

    @Test
    void wrapsProviderErrorsWithoutLeakingSensitiveDetails() {
        ResiliencePolicy policy = new ResiliencePolicy();

        ResiliencePolicy.ResilienceFailure failure = assertThrows(
                ResiliencePolicy.ResilienceFailure.class,
                () -> policy.protectProvider("fetch catalog", () -> {
                    throw new IllegalStateException("token=abc123");
                })
        );

        assertEquals(ResiliencePolicy.FailureDomain.PROVIDER, failure.failureDomain());
        assertEquals("fetch catalog", failure.operation());
        assertTrue(failure.retryable());
        assertFalse(failure.getMessage().contains("abc123"));
    }

    @Test
    void wrapsFilesystemErrorsAsNonRetryableFailures() {
        ResiliencePolicy policy = new ResiliencePolicy();

        ResiliencePolicy.ResilienceFailure failure = assertThrows(
                ResiliencePolicy.ResilienceFailure.class,
                () -> policy.protectFilesystem("write organized track", () -> {
                    throw new IOException("disk full");
                })
        );

        assertEquals(ResiliencePolicy.FailureDomain.FILESYSTEM, failure.failureDomain());
        assertEquals("write organized track", failure.operation());
        assertFalse(failure.retryable());
        assertTrue(failure.getMessage().contains("filesystem failure"));
    }
}
