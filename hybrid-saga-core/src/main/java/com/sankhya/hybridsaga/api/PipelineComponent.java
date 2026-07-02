package com.sankhya.hybridsaga.api;

/**
 * The base contract for all components in the pipeline.
 * <p>
 * This interface is sealed to ensure the pipeline only processes two types of components:
 * {@link TransactionalComponent} (supports rollback) and {@link NonTransactionalComponent}
 * (forward-only). This design guarantees exhaustive pattern matching and prevents unsupported
 * component types from being introduced.
 *
 * <h2>Type Parameters</h2>
 * <ul>
 * <li>{@code M} - The <strong>master request</strong> type. This is the primary payload flowing
 * through the pipeline and remains constant across all components.</li>
 * <li>{@code R} - The <strong>result</strong> type produced by {@link #forward(ForwardContext)}. Because
 * this varies per component, a pipeline registry handles mixed components as {@code PipelineComponent<M, ?>},
 * and results are retrieved from the context using a {@link Class} token.</li>
 * </ul>
 *
 * @param <M> the master request type (typically accessed via the context)
 * @param <R> the result type produced by {@link #forward(ForwardContext)} and stored in the context
 */
public sealed interface PipelineComponent<M, R> permits TransactionalComponent, NonTransactionalComponent {

    /**
     * Returns the unique identifier for this component.
     * <p>
     * This name serves as the source of truth for registering the component and acts as the key
     * when storing the {@link #forward(ForwardContext)} result in the shared context.
     * Implementations should typically return a constant.
     * <p>
     * <strong>ToDo: Registration Contract:</strong>
     * To prevent accidental null leakage, the pipeline engine will enforce this strictly at startup.
     * If a component attempts to register with a {@code null} operation name, the engine will immediately
     * throw an initialization exception.
     *
     * @return the unique operation name; must never be {@code null}
     */
    OperationName operationName();

    /**
     * Executes the component's main forward logic. The returned result is recorded in the shared context
     * under this component's {@link #operationName()}.
     * <p>
     * <strong>Stateless Design:</strong> Components must remain entirely stateless. Any required data
     * or generated output should be handled via the provided context, which exposes the master
     * request ({@code M}) and the outputs of previous steps.
     * <p>
     * <strong>Failure and Compensation:</strong> If this method throws an exception, the pipeline
     * marks the component as failed, halts execution, and triggers the compensation flow. This will
     * roll back any previously executed {@link TransactionalComponent}s in reverse order.
     * <p>
     * <strong>To prevent a rollback, you must catch and handle your own errors.</strong> Any exception
     * (including any {@link RuntimeException}) that escapes this method will automatically
     * initiate the compensation process.
     * <p>
     * This signature intentionally has NO checked exceptions. Implementations should throw
     * {@link RuntimeException}s on failure, as the underlying engine will catch general {@link Throwable}s.
     *
     * @param context the execution environment containing the master request and prior outputs
     * @return the result of the forward operation, or {@code null} if this component does not produce a value
     */
    R forward(ForwardContext<M> context);
}