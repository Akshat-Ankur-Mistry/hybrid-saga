package com.sankhya.hybridsaga.api;

/**
 * A pipeline component that fully participates in the Saga compensation pattern.
 * <p>
 * In addition to the standard forward action, this component provides a {@link #compensate(CompensationContext)}
 * method. If a downstream component fails, the pipeline engine will invoke this method to logically
 * undo the work previously completed by the forward action.
 *
 * <h2>"Transactional" means Saga-Compensating, NOT ACID</h2>
 * In this architecture, the word "transactional" describes participation in the Saga rollback protocol,
 * not a traditional database transaction.
 * <p>
 * Compensation here is purely <em>logical</em>. For example, if the forward step charged a credit card
 * or reserved inventory, the compensation step must explicitly issue a refund or release that inventory.
 * Do <strong>not</strong> attempt to wire this to a JDBC rollback; by the time the compensation phase
 * runs, the forward operation's database transaction has already been permanently committed.
 *
 * <h2>The Compensation Contract</h2>
 * When writing a transactional component, you must adhere to these rules:
 * <ul>
 * <li><strong>Conditional Execution:</strong> Compensation is only invoked if this specific component's
 * forward action completed successfully.</li>
 * <li><strong>Reverse Order:</strong> The engine triggers compensations in the exact reverse order
 * of the forward execution sequence.</li>
 * <li><strong>Idempotency & Retries:</strong> The pipeline engine will <strong>not</strong> issue automatic retries
 * for failed compensations; any retry mechanism must be explicitly implemented by the developer. However,
 * your compensate logic must still be idempotent so it is perfectly safe to run multiple times
 * (e.g., if triggered by external or custom application retries).</li>
 * <li><strong>Targeted Undo (Isolated State):</strong> The compensation context will give you access
 * <em>only</em> to the master request and the exact result produced by your own forward step. It will
 * <strong>not</strong> provide access to the outputs of other components. It is expected that a component
 * can fully revert its state using only its own forward response and (rarely) the master request.</li>
 * <li><strong>Resilience & Cascading Failures:</strong> If your {@code compensate} method throws an exception,
 * the engine will log the failure but <em>continue</em> compensating the remaining upstream components. It
 * will not abandon the rollback. However, be aware of standard Saga behavior: if upstream actions logically
 * depend on the successful rollback of your step, those subsequent compensations will likely fail as well.</li>
 * </ul>
 *
 * <h2>Extensibility Design</h2>
 * While the parent {@link PipelineComponent} is tightly {@code sealed} to restrict the component types to exactly two,
 * this interface is explicitly {@code non-sealed}. This acts as an open leaf in the hierarchy, allowing developers
 * to implement as many custom transactional steps as the application requires.
 *
 * @param <M> the master request type shared by every component in the pipeline
 * @param <R> the result type produced by the forward action, which is later handed back to the compensation action
 */
public non-sealed interface TransactionalComponent<M, R> extends PipelineComponent<M, R> {

    /**
     * Logically undoes the work performed by this component's previously-successful forward action.
     *
     * @param context the isolated compensation state. This provides secure, strongly-typed access to
     *                the master request and the specific result produced by this component's forward execution.
     */
    void compensate(CompensationContext<M, R> context);
}