package com.sankhya.hybridsaga.api;

/**
 * The absolute root exception for the entire application.
 * <p>
 * Every custom exception raised by the system—whether it is a framework-level fault, a pipeline
 * configuration error, or a domain-specific business failure—must ultimately inherit from this base class.
 *
 * <h2>Unified Error Handling</h2>
 * Establishing a single root exception creates a cohesive error hierarchy. It allows global exception
 * handlers, API gateways, or top-level orchestrators to use a single {@code catch (HybridSagaException e)}
 * block to trap and format all application-level faults in one standardized way.
 *
 * <h2>Unchecked by Design</h2>
 * By extending {@link RuntimeException}, this hierarchy deliberately avoids checked exceptions.
 * This design keeps core API signatures (such as {@code forward} and {@code compensate}) clean and
 * prevents developers from writing forced, boilerplate {@code try-catch} blocks for faults they
 * cannot meaningfully recover from at the component level.
 *
 * <h2>Usage Strategy for Developers</h2>
 * <ul>
 * <li><strong>Subclassing:</strong> Avoid throwing this base class directly. Instead, create targeted
 * subclasses (e.g., {@code MissingContextValueException}, {@code InvalidComponentStateException})
 * that clearly express the exact nature of the failure.</li>
 * <li><strong>Pipeline Impact:</strong> Because the pipeline engine catches general {@link Throwable}s,
 * any subclass of {@code HybridSagaException} thrown during a component's forward execution will
 * instantly fail that step and trigger the Saga compensation process.</li>
 * </ul>
 */
public class HybridSagaException extends RuntimeException {

    public HybridSagaException(String message) {
        super(message);
    }

    public HybridSagaException(String message, Throwable cause) {
        super(message, cause);
    }
}