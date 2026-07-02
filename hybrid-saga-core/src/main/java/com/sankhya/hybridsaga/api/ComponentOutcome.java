package com.sankhya.hybridsaga.api;

import java.util.Objects;
import java.util.Optional;

/**
 * An immutable record representing the final execution state of a single component within a pipeline run.
 * <p>
 * This is purely a data transfer object designed for logging, testing assertions, and post-run pipeline inspection.
 *
 * <h2>Design: Heterogeneous Values Typed as {@code Object}</h2>
 * Because a pipeline processes components that produce varying return types, a collection of outcomes
 * cannot share a single generic type signature. Therefore, the {@link #value()} is stored as a raw {@code Object}.
 * To read this value safely, the {@link #valueAs(Class)} method provides a centralized, type-safe downcast,
 * eliminating the need for {@code @SuppressWarnings("unchecked")} in user code.
 *
 * <h2>Design: State-Enforcing Static Factories</h2>
 * To guarantee state consistency, this record uses explicit static factory methods (e.g., {@code success},
 * {@code failed}) rather than forcing developers to use the raw four-argument constructor. This ensures
 * invariants are respected—for example, a {@code failed} outcome will always carry an error but never a value.
 *
 * @param operation the unique identity of the component this outcome belongs to (never {@code null})
 * @param status    the terminal state of the component (never {@code null})
 * @param value     the forward result (populated only for {@link Outcome#SUCCESS} or {@link Outcome#COMPENSATED}; otherwise {@code null})
 * @param error     the exception that caused the failure (populated only for {@link Outcome#FAILED} or {@link Outcome#COMPENSATION_FAILED}; otherwise {@code null})
 */
public record ComponentOutcome(OperationName operation, Outcome status, Object value, Throwable error) {

    /**
     * Validates the core invariants that every outcome must satisfy, regardless of how it was constructed.
     */
    public ComponentOutcome {
        Objects.requireNonNull(operation, "operation must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Constructs a successful outcome carrying the component's (possibly {@code null}) result value.
     */
    public static ComponentOutcome success(OperationName operation, Object value) {
        return new ComponentOutcome(operation, Outcome.SUCCESS, value, null);
    }

    /**
     * Constructs a failed outcome carrying the exact exception that halted execution.
     */
    public static ComponentOutcome failed(OperationName operation, Throwable error) {
        return new ComponentOutcome(operation, Outcome.FAILED, null, Objects.requireNonNull(error, "error must not be null"));
    }

    /**
     * Constructs a canceled outcome, indicating the component's execution was aborted before completion.
     */
    public static ComponentOutcome cancelled(OperationName operation) {
        return new ComponentOutcome(operation, Outcome.CANCELLED, null, null);
    }

    /**
     * Constructs a skipped outcome, indicating the component was intentionally bypassed.
     */
    public static ComponentOutcome skipped(OperationName operation) {
        return new ComponentOutcome(operation, Outcome.SKIPPED, null, null);
    }

    /**
     * @return {@code true} if this outcome represents a successful forward execution.
     */
    public boolean isSuccess() {
        return status == Outcome.SUCCESS;
    }

    /**
     * Safely retrieves the {@link #value()} by casting it to the requested type.
     *
     * @param type the expected class type of the value
     * @param <T>  the generic type of the value
     * @return the value cast to {@code type}, or {@link Optional#empty()} if no value is present
     * @throws ContextTypeMismatchException if a value is present but cannot be assigned to the requested type
     */
    public <T> Optional<T> valueAs(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        if (value == null) {
            return Optional.empty();
        }
        if (!type.isInstance(value)) {
            throw new ContextTypeMismatchException(operation, type, value.getClass());
        }
        return Optional.of(type.cast(value));
    }
}