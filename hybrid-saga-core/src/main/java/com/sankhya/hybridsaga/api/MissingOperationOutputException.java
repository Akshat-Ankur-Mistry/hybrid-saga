package com.sankhya.hybridsaga.api;

/**
 * Thrown by {@link ContextView#require(OperationName, Class)} when no result is stored under the requested
 * operation name.
 * <p>
 * This indicates that a component asked for an upstream output that was never produced. This is usually due
 * to a misordered pipeline (e.g., the producer runs after, or in the same parallel step as, the consumer)
 * or a misspelled operation name.
 * <p>
 * If the absence of an output is an expected, handled case, use {@link ContextView#find(OperationName, Class)} instead.
 * * <p>
 * <strong>TODO: Clarify Explicit Null Output Behavior</strong>
 * <br>
 * We need to determine and document what happens if a component's {@code forward} phase genuinely and
 * intentionally returns {@code null}. Does reading that result via {@code require()} throw this exception
 * (treating it as missing), or does it successfully return {@code null}? The context implementation must
 * explicitly distinguish between "no result recorded" and "result recorded as null".
 */
public class MissingOperationOutputException extends HybridSagaException {

    private final transient OperationName operation;

    public MissingOperationOutputException(OperationName operation) {
        super("No output is available for operation '" + operation
                + "'. It may not have run yet (same-step sibling or later step) or the name may be misspelled.");
        this.operation = operation;
    }

    /** * @return the operation name that had no stored output
     */
    public OperationName operation() {
        return operation;
    }
}