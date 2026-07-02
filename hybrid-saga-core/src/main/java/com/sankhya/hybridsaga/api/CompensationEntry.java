package com.sankhya.hybridsaga.api;

import java.util.Objects;

/**
 * An immutable record representing the result of a single compensation attempt during a pipeline rollback.
 * <p>
 * This acts as a single line item within a broader {@link CompensationReport}.
 *
 * @param operation the identity of the transactional component that was compensated (never {@code null})
 * @param status    the final outcome of the compensation attempt (strictly restricted to {@link Outcome#COMPENSATED}
 *                  or {@link Outcome#COMPENSATION_FAILED})
 * @param error     the exception thrown during a failed compensation; {@code null} if successful
 */
public record CompensationEntry(OperationName operation, Outcome status, Throwable error) {

    /**
     * Validates the core invariants that every compensation entry must satisfy.
     * * @throws NullPointerException if {@code operation} or {@code status} is null
     *
     * @throws IllegalArgumentException if {@code status} is not a valid compensation outcome
     */
    public CompensationEntry {
        Objects.requireNonNull(operation, "operation must not be null");
        Objects.requireNonNull(status, "status must not be null");

        if (status != Outcome.COMPENSATED && status != Outcome.COMPENSATION_FAILED) {
            throw new IllegalArgumentException(
                    "Invalid status for CompensationEntry. Expected COMPENSATED or COMPENSATION_FAILED, but received: " + status
            );
        }
    }

    /**
     * Constructs a successful compensation entry.
     */
    public static CompensationEntry compensated(OperationName operation) {
        return new CompensationEntry(operation, Outcome.COMPENSATED, null);
    }

    /**
     * Constructs a failed compensation entry, carrying the exact exception that caused the failure.
     */
    public static CompensationEntry failed(OperationName operation, Throwable error) {
        return new CompensationEntry(operation, Outcome.COMPENSATION_FAILED, Objects.requireNonNull(error, "error must not be null"));
    }

    /**
     * @return {@code true} if the compensation attempt completed successfully.
     */
    public boolean isSuccess() {
        return status == Outcome.COMPENSATED;
    }
}