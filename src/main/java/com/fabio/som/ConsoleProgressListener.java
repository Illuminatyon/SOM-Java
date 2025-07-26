package com.fabio.som;

/**
 * A console-based implementation of the ProgressListener interface.
 * 
 * This class provides a simple implementation that prints progress information
 * to the console, making it suitable for command-line applications.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class ConsoleProgressListener implements ProgressListener {
    
    /** The length of the progress bar in characters */
    private static final int PROGRESS_BAR_LENGTH = 50;
    
    /** The last reported progress percentage, used to avoid excessive updates */
    private int lastReportedPercentage = -1;
    
    /** The name of the current operation */
    private String currentOperation;
    
    /**
     * Called when a long-running operation starts.
     * 
     * @param operationName The name of the operation that is starting
     * @param totalSteps The total number of steps in the operation, or -1 if unknown
     */
    @Override
    public void onOperationStart(String operationName, long totalSteps) {
        this.currentOperation = operationName;
        System.out.println("Starting operation: " + operationName);
        
        if (totalSteps > 0) {
            System.out.println("Total steps: " + totalSteps);
        } else {
            System.out.println("Total steps: unknown");
        }
        
        // Reset the last reported percentage
        lastReportedPercentage = -1;
    }
    
    /**
     * Called to report progress during a long-running operation.
     * 
     * @param currentStep The current step number
     * @param totalSteps The total number of steps, or -1 if unknown
     * @param message Optional message describing the current step
     */
    @Override
    public void onProgressUpdate(long currentStep, long totalSteps, String message) {
        if (totalSteps <= 0) {
            // If total steps is unknown, just print the current step
            System.out.println("Progress: " + currentStep + " steps completed" + 
                              (message != null ? " - " + message : ""));
            return;
        }
        
        // Calculate the percentage of completion
        int percentage = (int)((currentStep * 100) / totalSteps);
        
        // Only update if the percentage has changed significantly (at least 1%)
        if (percentage > lastReportedPercentage) {
            lastReportedPercentage = percentage;
            
            // Create a progress bar
            StringBuilder progressBar = new StringBuilder("[");
            int completedChars = (int)((PROGRESS_BAR_LENGTH * percentage) / 100);
            
            for (int i = 0; i < PROGRESS_BAR_LENGTH; i++) {
                if (i < completedChars) {
                    progressBar.append("=");
                } else if (i == completedChars) {
                    progressBar.append(">");
                } else {
                    progressBar.append(" ");
                }
            }
            
            progressBar.append("] ").append(percentage).append("%");
            
            // Add the message if provided
            if (message != null && !message.isEmpty()) {
                progressBar.append(" - ").append(message);
            }
            
            // Print the progress bar
            System.out.print("\r" + progressBar);
            
            // If we're at 100%, add a newline
            if (percentage == 100) {
                System.out.println();
            }
            
            // Flush to ensure immediate display
            System.out.flush();
        }
    }
    
    /**
     * Called when a long-running operation completes.
     * 
     * @param operationName The name of the operation that completed
     * @param successful Whether the operation completed successfully
     * @param message Optional message describing the outcome
     */
    @Override
    public void onOperationComplete(String operationName, boolean successful, String message) {
        if (successful) {
            System.out.println("Operation completed successfully: " + operationName);
        } else {
            System.out.println("Operation failed: " + operationName);
        }
        
        if (message != null && !message.isEmpty()) {
            System.out.println(message);
        }
        
        // Reset the current operation
        this.currentOperation = null;
    }
    
    /**
     * Called to report memory usage during a long-running operation.
     * 
     * @param usedMemoryMB The amount of memory currently being used, in megabytes
     * @param totalMemoryMB The total amount of memory available, in megabytes
     */
    @Override
    public void onMemoryUsage(long usedMemoryMB, long totalMemoryMB) {
        double percentageUsed = (double)usedMemoryMB / totalMemoryMB * 100;
        System.out.printf("Memory usage: %.1f MB / %.1f MB (%.1f%%)%n", 
                         (double)usedMemoryMB, (double)totalMemoryMB, percentageUsed);
    }
}