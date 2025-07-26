package com.fabio.som;

/**
 * Interface for reporting progress during long-running operations.
 * 
 * This interface allows components to report progress updates during
 * operations like data loading, normalization, and SOM training.
 * Implementations can use these updates to provide feedback to users
 * through the UI or console.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public interface ProgressListener {
    
    /**
     * Called when a long-running operation starts.
     * 
     * @param operationName The name of the operation that is starting
     * @param totalSteps The total number of steps in the operation, or -1 if unknown
     */
    void onOperationStart(String operationName, long totalSteps);
    
    /**
     * Called to report progress during a long-running operation.
     * 
     * @param currentStep The current step number
     * @param totalSteps The total number of steps, or -1 if unknown
     * @param message Optional message describing the current step
     */
    void onProgressUpdate(long currentStep, long totalSteps, String message);
    
    /**
     * Called when a long-running operation completes.
     * 
     * @param operationName The name of the operation that completed
     * @param successful Whether the operation completed successfully
     * @param message Optional message describing the outcome
     */
    void onOperationComplete(String operationName, boolean successful, String message);
    
    /**
     * Called to report memory usage during a long-running operation.
     * 
     * @param usedMemoryMB The amount of memory currently being used, in megabytes
     * @param totalMemoryMB The total amount of memory available, in megabytes
     */
    void onMemoryUsage(long usedMemoryMB, long totalMemoryMB);
}