package com.sankhya.hybridsaga.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The comprehensive, terminal record of a pipeline execution.
 * <p>
 * The engine returns this object as pure <strong>data</strong> rather than throwing exceptions for failures.
 *
 * <h2>Design: Result-as-Data vs. Exceptions</h2>
 * In a Saga architecture, orchestration outcomes—such as component failures and subsequent
 * rollbacks—are expected, normal lifecycle events, not programming errors. Therefore, they are safely
 * packaged into this result object for inspection. Exceptions are strictly reserved for framework-level
 * misuse (e.g., misconfigured pipelines or missing context values).
 * <p>
 * Callers dictate the control flow by evaluating the {@link #status()} and inspecting the
 * {@link #outcomes()} and {@link #compensationReport()}, rather than relying on heavy {@code try-catch} blocks.
 *
 * <h2>Design: True Immutability</h2>
 * To ensure the execution trace cannot be modified post-run, the constructor applies a defensive copy
 * to the {@code outcomes} list using {@link List#copyOf}.
 *
 * @param status             the overarching terminal status of the pipeline (never {@code null})
 * @param outcomes           the chronologically ordered outcomes of every component that attempted execution (never {@code null}; securely copied)
 * @param failureCause       the exception that halted the forward pipeline execution, or {@code null} if the run completed successfully
 * @param compensationReport the detailed record of the rollback phase; will be {@link CompensationReport#empty()} if no compensation was required
 */
public record PipelineResult(
        PipelineStatus status,
        List<ComponentOutcome> outcomes,
        Throwable failureCause,
        CompensationReport compensationReport) {

    /**
     * Validates the core invariants and secures the collections against modification.
     */
    public PipelineResult {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(outcomes, "outcomes must not be null");
        outcomes = List.copyOf(outcomes); // defensive immutable snapshot
        Objects.requireNonNull(compensationReport, "compensationReport must not be null");
    }

    /**
     * @return {@code true} if the entire pipeline executed successfully without any failures or compensations
     */
    public boolean isCompleted() {
        return status == PipelineStatus.COMPLETED;
    }

    /**
     * Safely retrieves the exception that triggered the pipeline failure, if one occurred.
     *
     * @return the failure cause wrapped in an {@link Optional}, or {@link Optional#empty()} if the pipeline completed successfully
     */
    public Optional<Throwable> failure() {
        return Optional.ofNullable(failureCause);
    }
}