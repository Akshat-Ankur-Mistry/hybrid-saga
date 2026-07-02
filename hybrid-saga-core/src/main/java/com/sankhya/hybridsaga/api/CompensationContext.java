package com.sankhya.hybridsaga.api;

/**
 * The execution environment provided to a {@link TransactionalComponent} during its compensation phase.
 * <p>
 * This context delivers exactly what a component needs to logically undo its previous work:
 * the original master request and the specific, strongly-typed result produced by its own
 * forward execution.
 *
 * <h2>Design: Strict Isolation</h2>
 * Notice that this interface deliberately does <strong>not</strong> extend {@link ForwardContext}.
 * During compensation, a component must perform a targeted, isolated rollback. It is explicitly
 * forbidden from reading the outputs of other components. By restricting this interface to just
 * two methods, the compiler physically prevents developers from accessing the wider pipeline state
 * during a rollback.
 *
 * <h2>Targeted Undo</h2>
 * To successfully undo an action, a component usually needs the exact identifier or state it
 * produced during the forward phase (e.g., the specific reservation ID to release, or the payment ID
 * to refund). Passing this result back as a strongly-typed parameter ({@code R}) means the
 * {@code compensate} method does not need to re-query or manually cast its own output.
 *
 * @param <M> the type of the master request flowing through the pipeline
 * @param <R> the type of the result produced by this component's forward action
 */
public interface CompensationContext<M, R> {

    /**
     * Returns the primary payload that initiated the pipeline execution.
     * <p>
     * Every component in the pipeline receives the exact same instance of this request.
     * By convention, components must treat this object as strictly read-only and immutable.
     *
     * @return the master request
     */
    M masterRequest();

    /**
     * Retrieves the exact value this component returned during the {@link PipelineComponent#forward}
     * execution that is now being compensated.
     * <p>
     * This provides the necessary state to perform a targeted, idempotent rollback.
     *
     * @return the forward result (maybe {@code null} if the forward action explicitly returned null)
     */
    R forwardResult();
}