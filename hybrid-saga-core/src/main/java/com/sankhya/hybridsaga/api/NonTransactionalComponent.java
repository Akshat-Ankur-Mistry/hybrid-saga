package com.sankhya.hybridsaga.api;

/**
 * A forward-only component in the pipeline.
 * <p>
 * This component only executes a {@link #forward(ForwardContext)} action and does not support rollback.
 * Therefore, its actions will <strong>never be compensated</strong>.
 * <p>
 * <strong>Warning on Failures:</strong> Even though this component cannot be rolled back,
 * if its {@link #forward(ForwardContext)} method throws an exception, it <strong>will</strong> halt the
 * pipeline and trigger the compensation flow, rolling back any previously executed
 * {@link TransactionalComponent}s.
 *
 * <h2>When to use this</h2>
 * Use this for side-effect-tolerant work that cannot or does not need to be undone—such as
 * sending a notification, logging a state, or enriching the information. Because
 * it is not a fully participating Saga component, it does not require a rollback definition.
 *
 * <h2>Intent over capability</h2>
 * This interface adds no new methods to {@link PipelineComponent}; it simply inherits
 * {@link #operationName()} and {@link #forward(ForwardContext)}. Its purpose is to structurally classify
 * the component as non-compensating, allowing the pipeline engine to treat it differently from
 * a {@link TransactionalComponent}. It remains {@code non-sealed} so developers can implement
 * it freely.
 *
 * <p>
 * <strong>TODO: Error Swallowing / Best-Effort Toggle</strong>
 * <br>
 * We need to introduce a way for these components to automatically swallow errors so they can act
 * as true "best-effort" steps. Since these are non-critical and do not participate in the Saga
 * compensation themselves, we can safely add a configuration to automatically log the error without
 * failing the component.
 * <ul>
 * <li>Default behavior must remain as-is: unhandled errors fail the step and trigger compensation.</li>
 * <li>The new toggle should ONLY be available for {@code NonTransactionalComponent}s.</li>
 * <li>Errors emitted from a {@code TransactionalComponent} must always strictly trigger compensation.</li>
 * </ul>
 *
 * @param <M> the master request type shared by every component in the pipeline
 * @param <R> the result type produced by {@link #forward(ForwardContext)} and stored in the shared context
 */
public non-sealed interface NonTransactionalComponent<M, R> extends PipelineComponent<M, R> {
    // Marker leaf: forward-only. All behavior is inherited from PipelineComponent.
}