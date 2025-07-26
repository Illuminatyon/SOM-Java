package com.fabio.som;

import java.io.File;
import java.util.Scanner;

/**
 * Main application class for visualizing Self-Organizing Maps (SOM).
 * 
 * This class provides a simple command-line interface for running the SOM algorithm
 * and visualizing the results. It allows users to load data, train the SOM, and
 * display the resulting classification grid.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class SOMVisualizer {

    /**
     * Main entry point for the application.
     * 
     * @param args Command-line arguments (optional: path to input CSV file)
     */
    public static void main(String[] args) {
        System.out.println("=== Self-Organizing Map (SOM) Visualizer ===");
        System.out.println("By Fabio GUERREIRO MARQUES");
        System.out.println();
        
        try (Scanner scanner = new Scanner(System.in)) {
            // Get the data file path
            String filename = getDataFilePath(args, scanner);
            
            // Initialize the SOM processor
            System.out.println("Loading data from " + filename + "...");
            SOMProcessor processor = new SOMProcessor(filename);
            System.out.println("Data loaded successfully: " + processor.getDataPoints().size() + " data points");
            
            // Display data information
            if (!processor.getDataPoints().isEmpty()) {
                System.out.println("Number of dimensions: " + processor.getDataPoints().get(0).getDimensionCount());
            }
            
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
            System.out.println("\nGenerating neuron grid...");
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
            
            System.out.println("\nSOM visualization completed successfully.");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the data file path from command-line arguments or user input.
     * 
     * @param args Command-line arguments
     * @param scanner Scanner for reading user input
     * @return The path to the data file
     */
    private static String getDataFilePath(String[] args, Scanner scanner) {
        String filename = null;
        
        // Check if file path was provided as command-line argument
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
            while (filename == null) {
                System.out.print("Please enter the path to the CSV data file: ");
                filename = scanner.nextLine().trim();
                
                if (filename.isEmpty()) {
                    System.out.println("Error: Filename cannot be empty");
                    filename = null;
                    continue;
                }
                
                File file = new File(filename);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("Error: File not found: " + filename);
                    filename = null;
                }
            }
        }
        
        return filename;
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