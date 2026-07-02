package com.sankhya.hybridsaga.api;

import java.util.Objects;

/**
 * A strongly-typed identifier for pipeline components.
 * <p>
 * This name serves as the unique key used to register a component within the pipeline
 * and to store its execution results in the shared context. By wrapping a standard {@link String},
 * we ensure type safety and guarantee that the name is always valid by construction.
 * <p>
 * ToDo: let's ensure no duplicates. we might require another validation component post component registration
 * </p>
 *
 * @param value the raw string value of the operation name (will be trimmed and validated)
 */
public record OperationName(String value) {

    /**
     * Compact constructor to normalize and validate the input.
     * <p>
     * Guarantees that an {@code OperationName} cannot be instantiated with a null, empty,
     * or whitespace-only value.
     *
     * @throws NullPointerException     if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is blank (empty or whitespace-only)
     */
    public OperationName {
        Objects.requireNonNull(value, "operation name must not be null");
        value = value.trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("operation name must not be blank");
        }
    }

    /**
     * A factory method mirroring the canonical constructor.
     * <p>
     * This provides a more fluent syntax at call sites (e.g., {@code OperationName.of("my-step")})
     * and is useful as a method reference ({@code OperationName::of}) when parsing or mapping
     * lists of names from configurations.
     *
     * @param value the raw operation name (will be trimmed and validated)
     * @return a validated {@code OperationName} instance
     */
    public static OperationName of(String value) {
        return new OperationName(value);
    }

    /**
     * Returns the raw operation name.
     * <p>
     * Overriding this allows an {@code OperationName} instance to be passed directly into
     * log messages or string formatting without needing to call {@code .value()} explicitly.
     *
     * @return the raw, non-null operation name string
     */
    @Override
    public String toString() {
        return value;
    }
}