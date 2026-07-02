package com.sankhya.hybridsaga.api;

/**
 * Thrown by {@link ContextView} reads when a stored value exists but is not assignable to the {@link Class}
 * type the caller asked for.
 *
 * <p>Centralizing the cast and raising this typed exception - instead of letting a raw {@link ClassCastException}
 * surface deep inside caller code - means component authors never write an unchecked cast or
 * {@code @SuppressWarnings}, and the error message names the operation and both types so the mismatch is obvious.
 */
public class ContextTypeMismatchException extends HybridSagaException {

    private final transient OperationName operation;
    private final transient Class<?> expectedType;
    private final transient Class<?> actualType;

    public ContextTypeMismatchException(OperationName operation, Class<?> expectedType, Class<?> actualType) {
        super("Output of operation '" + operation + "' is a " + actualType.getName()
                + " but was requested as " + expectedType.getName() + ".");
        this.operation = operation;
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    /**
     * @return the operation whose stored value was mis-typed.
     */
    public OperationName operation() {
        return operation;
    }

    /**
     * @return the type the caller requested.
     */
    public Class<?> expectedType() {
        return expectedType;
    }

    /**
     * @return the actual runtime type of the stored value.
     */
    public Class<?> actualType() {
        return actualType;
    }
}