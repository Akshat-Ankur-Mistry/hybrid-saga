package com.sankhya.hybridsaga.api;

/**
 * Represents the terminal state of a component's execution within a pipeline run.
 * <p>
 * These statuses are recorded on the {@link ComponentOutcome} as pure data. They are not thrown
 * as exceptions, allowing an orchestrator to fully inspect the lifecycle of every component—including
 * identifying exactly where a failure occurred and which components were successfully rolled back.
 */
public enum Outcome {

    /**
     * The component's forward action completed normally. Its result (if any) has been safely
     * recorded in the shared context.
     */
    SUCCESS,

    /**
     * The component's forward action threw an exception.
     * <p>
     * <strong>Impact:</strong> This halts pipeline progression and immediately triggers the Saga
     * compensation flow, rolling back any previously executed transactional components.
     */
    FAILED,

    /**
     * The component was terminated before it could finish executing.
     * <p>
     * This typically occurs during parallel step execution, where the failure of one sibling
     * component causes the engine to aggressively cancel the remaining concurrent tasks, or
     * if a strict execution timeout elapses.
     */
    CANCELLED,

    /**
     * The component was intentionally bypassed by the pipeline (e.g., via conditional routing logic)
     * and did not execute.
     */
    SKIPPED,

    /**
     * The transactional component's forward action had previously succeeded, but a downstream failure
     * triggered a pipeline rollback. The component's {@code compensate} method has now successfully
     * completed its logical undo.
     */
    COMPENSATED,

    /**
     * The transactional component was invoked for rollback, but its {@code compensate} method threw
     * an exception.
     * <p>
     * <strong>Impact:</strong> This indicates a partial or broken Saga rollback. Operators must be
     * alerted to this state, as it typically requires manual intervention or reconciliation to fix
     * the underlying data inconsistency.
     */
    COMPENSATION_FAILED
}