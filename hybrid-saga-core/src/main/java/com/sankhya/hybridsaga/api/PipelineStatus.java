package com.sankhya.hybridsaga.api;

/**
 * Represents the final, terminal state of an entire pipeline execution.
 * <p>
 * This enum defines a strict, closed set of orchestration outcomes. By returning this as part
 * of the {@link PipelineResult}, orchestrators can use exhaustive {@code switch} statements
 * to handle every scenario explicitly—especially the critical {@link #COMPENSATION_INCOMPLETE} state.
 */
public enum PipelineStatus {

    /**
     * The pipeline executed completely from start to finish without any exceptions.
     * No compensation was triggered.
     */
    COMPLETED,

    /**
     * A component failed during execution, triggering a rollback. Every previously executed
     * {@link TransactionalComponent} successfully completed its compensation logic.
     * The system has been cleanly reverted to its initial state.
     */
    COMPENSATED,

    /**
     * A component failed, triggering a rollback, but one or more {@code compensate} methods
     * <em>also</em> threw exceptions.
     * <p>
     * <strong>Critical State:</strong> The system is likely in an inconsistent or partially
     * rolled-back state. Operators should inspect the {@link PipelineResult#compensationReport()}
     * to identify exactly which compensations failed and initiate manual intervention or reconciliation.
     */
    COMPENSATION_INCOMPLETE,

    /**
     * A component failed, halting the pipeline, but there were no prior successful
     * {@link TransactionalComponent}s to roll back. The system state remains clean.
     */
    FAILED_NO_COMPENSATION
}