package com.fabio.som;

import java.io.File;
import java.util.Scanner;

/**
 * Main application class for the Self-Organizing Map (SOM) implementation.
 * 
 * This class provides a command-line interface for interacting with the SOM,
 * allowing users to:
 * - Load data from CSV files
 * - View and analyze the data
 * - Train the SOM
 * - Visualize the results
 * 
 * The application guides users through the process of creating, training,
 * and analyzing a Self-Organizing Map for data classification and visualization.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class SOMApplication {

    /**
     * Main entry point for the application.
     * 
     * @param args Command-line arguments (optional: path to input CSV file)
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Process command-line arguments if provided
            String filename = null;
            if (args.length > 0) {
                filename = args[0];
                File file = new File(filename);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("Error: File not found: " + filename);
                    filename = null;
                }
            }
            
            // If no valid filename from command line, prompt user
            if (filename == null) {
                System.out.println("=== Self-Organizing Map (SOM) Application ===");
                System.out.print("Please enter the path to the CSV data file: ");
                filename = scanner.nextLine();
            }
            
            // Initialize the SOM processor
            SOMProcessor processor = null;
            try {
                processor = new SOMProcessor(filename);
                System.out.println("Data loaded successfully.");
            } catch (Exception e) {
                System.err.println("Error loading data: " + e.getMessage());
                System.exit(1);
            }
            
            // Display data information
            System.out.println("\nData Information:");
            System.out.println("Number of data points: " + processor.getDataPoints().size());
            if (!processor.getDataPoints().isEmpty()) {
                System.out.println("Number of dimensions: " + 
                                  processor.getDataPoints().get(0).getDimensionCount());
            }
            
            // Display random sample of data
            System.out.println("\nRandom sample of data points:");
            processor.printRandomizedDataPoints(processor.getDataPoints());
            
            // Calculate mean vector
            System.out.println("\nCalculating mean vector...");
            processor.calculateMeanVector();
            
            // Get interval bounds for random weight vectors
            double upperBound = promptForDouble(scanner, 
                                "Please enter the upper bound for the interval: ", 0.0, Double.MAX_VALUE);
            double lowerBound = promptForDouble(scanner, 
                                "Please enter the lower bound for the interval: ", 0.0, Double.MAX_VALUE);
            
            // Create random weight vectors
            System.out.println("\nCreating random weight vectors...");
            processor.createRandomWeightVectors(upperBound, lowerBound);
            
            // Generate neuron grid
            processor.generateNeuronGrid();
            
            // Choose training mode
            boolean useRandomOrder = promptForTrainingMode(scanner);
            
            // Train the SOM
            System.out.println("\nTraining the Self-Organizing Map...");
            processor.trainSOM(useRandomOrder);
            
            // Assign class labels
            System.out.println("\nAssigning class labels to neurons...");
            processor.assignClassLabels();
            
            // Display results
            System.out.println("\nClassification results:");
            processor.renameClassLabels();
            processor.printClassLabels();
            
            // Count class occurrences
            System.out.println("\nClass distribution:");
            processor.countClassLabels();
            
            System.out.println("\nSOM processing completed successfully.");
            
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Prompts the user for a double value within the specified range.
     * 
     * @param scanner The Scanner to use for input
     * @param prompt The message to display to the user
     * @param min The minimum allowed value (inclusive)
     * @param max The maximum allowed value (inclusive)
     * @return The validated double value entered by the user
     */
    private static double promptForDouble(Scanner scanner, String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine();
                value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    break;
                } else {
                    System.out.println("Value must be between " + min + " and " + max + ". Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
            }
        }
        return value;
    }
    
    /**
     * Prompts the user to select a training mode (random or sequential).
     * 
     * @param scanner The Scanner to use for input
     * @return true for random order, false for sequential order
     */
    private static boolean promptForTrainingMode(Scanner scanner) {
        while (true) {
            System.out.print("\nSelect training mode (0 for sequential, 1 for random): ");
            try {
                String input = scanner.nextLine();
                int mode = Integer.parseInt(input);
                if (mode == 0 || mode == 1) {
                    return mode == 1;
                } else {
                    System.out.println("Please enter either 0 or 1.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter either 0 or 1.");
            }
        }
    }
}