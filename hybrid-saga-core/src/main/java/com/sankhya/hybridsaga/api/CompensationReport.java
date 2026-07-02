package com.sankhya.hybridsaga.api;

import java.util.List;
import java.util.Objects;

/**
 * The complete, ordered record of a pipeline rollback phase.
 * <p>
 * This report contains a chronologically ordered list of {@link CompensationEntry} items, detailing
 * exactly which transactional components were invoked for rollback and whether those attempts succeeded.
 * The list order represents the actual execution sequence (which is the strict reverse of the forward execution).
 *
 * <h2>Design: True Immutability via Defensive Copying</h2>
 * To guarantee that this report cannot be tampered with after the pipeline concludes, the compact
 * constructor wraps the incoming entries in {@link List#copyOf(java.util.Collection)}. This ensures the
 * record owns a deeply immutable snapshot, preventing caller mutations and rejecting {@code null} elements outright.
 *
 * @param entries the chronologically ordered list of compensation results (never {@code null}; securely copied)
 */
public record CompensationReport(List<CompensationEntry> entries) {

    public CompensationReport {
        Objects.requireNonNull(entries, "entries must not be null");
        entries = List.copyOf(entries); // defensive immutable copy; also rejects null elements
    }

    /**
     * @return an empty report indicating that no compensation was required or attempted
     */
    public static CompensationReport empty() {
        return new CompensationReport(List.of());
    }

    /**
     * @return {@code true} if no compensations were executed
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * @return {@code true} if every attempted compensation succeeded. Returns {@code true} if the report is empty.
     */
    public boolean allSucceeded() {
        return entries.stream().allMatch(CompensationEntry::isSuccess);
    }

    /**
     * Extracts only the compensation attempts that failed.
     * <p>
     * This is particularly useful for alerting, monitoring, or triggering manual reconciliation
     * processes for partial or broken rollbacks.
     *
     * @return a list containing only the failed compensation entries
     */
    public List<CompensationEntry> failures() {
        return entries.stream().filter(e -> !e.isSuccess()).toList();
    }
}