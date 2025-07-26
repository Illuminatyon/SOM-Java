package com.fabio.som;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Core processor for Self-Organizing Maps (SOM).
 * 
 * This class is responsible for:
 * - Loading and normalizing input data
 * - Creating and managing the SOM grid (neuron matrix)
 * - Training the SOM using competitive learning
 * - Classifying data points using the trained SOM
 * - Visualizing and analyzing the results
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class SOMProcessor {
    /** List of original input data points */
    private List<DataPoint> dataPoints;

    /** Normalized version of the input data points */
    private List<DataPoint> normalizedDataPoints;

    /** Randomized subset of data points used for training */
    private List<DataPoint> randomTrainingSet;

    /** Array of shuffled indices for randomized data access */
    private int[] shuffledIndices;

    /** Array of mean values for each feature dimension */
    private double[] meanValues;

    /** Vector representing the global average of all data points */
    private List<Double> meanVector;

    /** Main grid (matrix) of neurons used in the SOM */
    private Neuron[][] neuronGrid;

    /** Submatrix used during neighborhood updates */
    private Neuron[][] neighborhoodGrid;

    /** List of neurons with the shortest distance to input vectors (BMUs) */
    private List<Neuron> bestMatchingUnits;

    /** Random number generator for consistent randomization */
    private final Random random = new Random();
    
    /** Default batch size for processing large datasets */
    private static final int DEFAULT_BATCH_SIZE = 1000;
    
    /** Current batch size for data processing */
    private int batchSize = DEFAULT_BATCH_SIZE;
    
    /** Flag indicating whether to use sampling for very large datasets */
    private boolean useSampling = false;
    
    /** Sampling rate for very large datasets (between 0 and 1) */
    private double samplingRate = 1.0;
    
    /** Progress listener for reporting loading and processing progress */
    private ProgressListener progressListener;

    /**
     * Constructs the SOMProcessor and loads data from the specified file.
     *
     * @param filename The name of the CSV file containing the input data
     * @throws IllegalArgumentException if the file cannot be read or processed
     */
    public SOMProcessor(String filename) {
        this(filename, new ConsoleProgressListener());
    }
    
    /**
     * Constructs the SOMProcessor with the specified progress listener and loads data from the specified file.
     *
     * @param filename The name of the CSV file containing the input data
     * @param progressListener The listener for reporting progress
     * @throws IllegalArgumentException if the file cannot be read or processed
     */
    public SOMProcessor(String filename, ProgressListener progressListener) {
        this.normalizedDataPoints = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
        this.progressListener = progressListener;
        
        try {
            System.out.println("Processing file: " + filename);
            loadDataFromFile(filename);
            normalizeData(this.dataPoints, this.normalizedDataPoints);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error opening or reading file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sets the batch size for processing large datasets.
     *
     * @param batchSize The batch size to use (must be positive)
     * @return This SOMProcessor instance for method chaining
     * @throws IllegalArgumentException if batchSize is not positive
     */
    public SOMProcessor setBatchSize(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be positive");
        }
        this.batchSize = batchSize;
        return this;
    }
    
    /**
     * Enables or disables data sampling for very large datasets.
     *
     * @param useSampling Whether to use sampling
     * @param samplingRate The sampling rate (between 0 and 1)
     * @return This SOMProcessor instance for method chaining
     * @throws IllegalArgumentException if samplingRate is not between 0 and 1
     */
    public SOMProcessor setSampling(boolean useSampling, double samplingRate) {
        if (samplingRate <= 0 || samplingRate > 1) {
            throw new IllegalArgumentException("Sampling rate must be between 0 and 1");
        }
        this.useSampling = useSampling;
        this.samplingRate = samplingRate;
        return this;
    }
    
    /**
     * Sets the progress listener for reporting progress during long-running operations.
     *
     * @param progressListener The progress listener to use
     * @return This SOMProcessor instance for method chaining
     */
    public SOMProcessor setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * Loads data from the specified CSV file using batch processing.
     *
     * @param filename The name of the file to load
     * @throws IOException if an I/O error occurs
     */
    private void loadDataFromFile(String filename) throws IOException {
        File file = new File(filename);
        
        // First pass: count lines to determine total size (for progress reporting)
        long totalLines = countLines(file) - 1; // Subtract 1 for header
        
        // Report memory usage before loading
        reportMemoryUsage();
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Loading data from " + filename, totalLines);
        }
        
        // Second pass: load data in batches
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            
            // Skip the header row and analyze it to determine column structure
            String headerLine = bufferedReader.readLine();
            int[] featureIndices = determineFeatureIndices(headerLine);
            int labelIndex = determineLabelIndex(headerLine);
            
            // Initialize batch processing variables
            List<DataPoint> batch = new ArrayList<>(batchSize);
            String line;
            long lineCount = 0;
            long processedCount = 0;
            Random samplingRandom = new Random();
            
            // Process the file in batches
            while ((line = bufferedReader.readLine()) != null) {
                lineCount++;
                
                // If sampling is enabled, only process a subset of the data
                if (useSampling && samplingRandom.nextDouble() > samplingRate) {
                    // Skip this line for sampling purposes
                    continue;
                }
                
                // Parse the line and create a DataPoint
                String[] values = line.split(",");
                
                try {
                    // Use the enhanced constructor with column mapping
                    DataPoint dataPoint = new DataPoint(values, featureIndices, labelIndex, 0.0);
                    batch.add(dataPoint);
                    processedCount++;
                    
                    // Report progress periodically
                    if (processedCount % 100 == 0 && progressListener != null) {
                        progressListener.onProgressUpdate(processedCount, totalLines, 
                                                         "Processed " + processedCount + " data points");
                    }
                    
                    // When batch is full, add to main list and clear batch
                    if (batch.size() >= batchSize) {
                        this.dataPoints.addAll(batch);
                        batch.clear();
                        
                        // Report memory usage after each batch
                        reportMemoryUsage();
                    }
                } catch (Exception e) {
                    // Log the error but continue processing
                    System.err.println("Error processing line " + lineCount + ": " + e.getMessage());
                }
            }
            
            // Add any remaining data points in the last batch
            if (!batch.isEmpty()) {
                this.dataPoints.addAll(batch);
            }
            
            // Report final progress
            if (progressListener != null) {
                progressListener.onProgressUpdate(processedCount, totalLines, 
                                                 "Completed loading " + processedCount + " data points");
                progressListener.onOperationComplete("Data loading", true, 
                                                    "Successfully loaded " + processedCount + " data points");
            }
        }
    }
    
    /**
     * Counts the number of lines in a file.
     *
     * @param file The file to count lines in
     * @return The number of lines in the file
     * @throws IOException if an I/O error occurs
     */
    private long countLines(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            
            long count = 0;
            while (bufferedReader.readLine() != null) {
                count++;
            }
            return count;
        }
    }
    
    /**
     * Determines which columns in the CSV file contain feature values.
     * This implementation assumes all columns except the ID column (first) and label column (last)
     * are feature columns, but can be extended for more complex formats.
     *
     * @param headerLine The header line from the CSV file
     * @return An array of indices indicating which columns contain feature values
     */
    private int[] determineFeatureIndices(String headerLine) {
        if (headerLine == null || headerLine.isEmpty()) {
            return new int[0];
        }
        
        String[] headers = headerLine.split(",");
        
        // Assume the first column is ID and the last column is the label
        // All columns in between are features
        int[] featureIndices = new int[headers.length - 2];
        for (int i = 0; i < featureIndices.length; i++) {
            featureIndices[i] = i + 1; // Skip the first column (ID)
        }
        
        return featureIndices;
    }
    
    /**
     * Determines which column in the CSV file contains the label.
     * This implementation assumes the last column is the label.
     *
     * @param headerLine The header line from the CSV file
     * @return The index of the label column
     */
    private int determineLabelIndex(String headerLine) {
        if (headerLine == null || headerLine.isEmpty()) {
            return -1;
        }
        
        String[] headers = headerLine.split(",");
        return headers.length - 1; // Assume the last column is the label
    }
    
    /**
     * Reports the current memory usage to the progress listener.
     */
    private void reportMemoryUsage() {
        if (progressListener != null) {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            progressListener.onMemoryUsage(usedMemory, totalMemory);
        }
    }

    /**
     * Gets the original (non-normalized) input data.
     *
     * @return The list of original DataPoint instances
     */
    public List<DataPoint> getDataPoints() {
        return this.dataPoints;
    }

    /**
     * Gets the normalized version of the input data.
     *
     * @return The list of normalized DataPoint instances
     */
    public List<DataPoint> getNormalizedDataPoints() {
        return this.normalizedDataPoints;
    }

    /**
     * Gets the randomized training data.
     *
     * @return The list of DataPoint instances in random order
     */
    public List<DataPoint> getRandomTrainingSet() {
        return this.randomTrainingSet;
    }

    /**
     * Gets the main SOM neuron grid.
     *
     * @return The 2D array of Neuron objects
     */
    public Neuron[][] getNeuronGrid() {
        return this.neuronGrid;
    }

    /**
     * Gets the neighborhood grid used during training.
     *
     * @return The 2D array of Neuron objects (subset of the main grid)
     */
    public Neuron[][] getNeighborhoodGrid() {
        return this.neighborhoodGrid;
    }

    /**
     * Gets the list of best matching neurons (BMUs).
     *
     * @return The list of best matching neurons
     */
    public List<Neuron> getBestMatchingUnits() {
        return this.bestMatchingUnits;
    }

    /**
     * Computes the mean vector from the normalized data.
     * This method calculates the average of each feature dimension across all
     * normalized data points and stores the result in the meanVector field.
     */
    public void calculateMeanVector() {
        if (this.normalizedDataPoints.isEmpty()) {
            throw new IllegalStateException("No normalized data available");
        }
        
        int dimensions = this.normalizedDataPoints.get(0).getDimensionCount();
        this.meanValues = new double[dimensions];
        this.meanVector = new ArrayList<>(dimensions);
        
        // Sum all dimensions
        for (DataPoint dataPoint : this.normalizedDataPoints) {
            double[] features = dataPoint.getFeatures();
            for (int i = 0; i < dimensions; i++) {
                meanValues[i] += features[i];
            }
        }
        
        // Calculate average
        System.out.println("Mean vector:");
        for (int i = 0; i < dimensions; i++) {
            double mean = meanValues[i] / this.dataPoints.size();
            meanVector.add(mean);
            System.out.print(mean + " ");
        }
        System.out.println();
    }

    /**
     * Creates a set of random weight vectors within specified intervals around the mean vector.
     * This method generates random weight vectors within the range defined by the mean vector
     * and the specified upper and lower bounds. The number of vectors is determined by the
     * formula 5 * sqrt(number of data points), rounded to the nearest multiple of 10.
     *
     * @param upperBound The upper bound for the intervals around the mean vector
     * @param lowerBound The lower bound for the intervals around the mean vector
     * @return A list of random DataPoint objects created within the specified intervals
     * @throws IllegalStateException if the mean vector has not been calculated
     */
    public List<DataPoint> createRandomWeightVectors(double upperBound, double lowerBound) {
        if (this.meanVector == null || this.meanVector.isEmpty()) {
            throw new IllegalStateException("Mean vector must be calculated first");
        }
        
        this.randomTrainingSet = new ArrayList<>();
        
        int dimensions = this.meanVector.size();
        double[] maxLimits = new double[dimensions];
        double[] minLimits = new double[dimensions];
        
        // Calculate limits for each dimension
        for (int i = 0; i < dimensions; i++) {
            maxLimits[i] = this.meanVector.get(i) + upperBound;
            minLimits[i] = this.meanVector.get(i) - lowerBound;
        }
        
        // Calculate number of vectors (5 * sqrt(n)), rounded to nearest multiple of 10
        int vectorCount = (int)(5.0 * Math.sqrt(this.dataPoints.size()));
        vectorCount = vectorCount - (vectorCount % 10);
        
        // Create random vectors
        for (int i = 0; i < vectorCount; i++) {
            DataPoint randomVector = new DataPoint(dimensions, minLimits, maxLimits);
            this.randomTrainingSet.add(randomVector);
        }
        
        return this.randomTrainingSet;
    }

    /**
     * Prints the features and labels of each data point in the provided list.
     *
     * @param dataPoints The list of DataPoint objects to print
     */
    public void printDataPoints(List<DataPoint> dataPoints) {
        for (DataPoint point : dataPoints) {
            double[] features = point.getFeatures();
            for (double feature : features) {
                System.out.print(feature + ",");
            }
            System.out.println(point.getLabel());
        }
        System.out.println();
    }

    /**
     * Prints the features and labels of data points in random order.
     *
     * @param dataPoints The list of DataPoint objects to print in random order
     */
    public void printRandomizedDataPoints(List<DataPoint> dataPoints) {
        // Initialize shuffled indices
        this.shuffledIndices = new int[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) {
            this.shuffledIndices[i] = i;
        }
        
        // Shuffle indices
        for (int i = 0; i < this.shuffledIndices.length; i++) {
            int randomIndex = random.nextInt(this.shuffledIndices.length);
            int temp = this.shuffledIndices[i];
            this.shuffledIndices[i] = this.shuffledIndices[randomIndex];
            this.shuffledIndices[randomIndex] = temp;
        }
        
        // Print data points in random order
        for (int i = 0; i < Math.min(10, dataPoints.size()); i++) {
            DataPoint point = dataPoints.get(this.shuffledIndices[i]);
            double[] features = point.getFeatures();
            for (double feature : features) {
                System.out.print(feature + ",");
            }
            System.out.println(point.getLabel());
        }
    }

    /**
     * Normalizes the data points by converting each vector to a unit vector.
     * This method uses batch processing for efficient memory usage with large datasets.
     *
     * @param sourceList The list of DataPoint objects to normalize
     * @param targetList The list where normalized DataPoint objects will be stored
     */
    public void normalizeData(List<DataPoint> sourceList, List<DataPoint> targetList) {
        if (sourceList == null || sourceList.isEmpty()) {
            return;
        }
        
        // Clear the target list before adding normalized points
        targetList.clear();
        
        // Report memory usage before normalization
        reportMemoryUsage();
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Normalizing data", sourceList.size());
        }
        
        // Process in batches for better memory management
        int totalSize = sourceList.size();
        int batchCount = (int) Math.ceil((double) totalSize / batchSize);
        
        for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
            // Calculate the start and end indices for this batch
            int startIndex = batchIndex * batchSize;
            int endIndex = Math.min(startIndex + batchSize, totalSize);
            
            // Create a temporary list for this batch
            List<DataPoint> normalizedBatch = new ArrayList<>(endIndex - startIndex);
            
            // Normalize each point in this batch
            for (int i = startIndex; i < endIndex; i++) {
                DataPoint point = sourceList.get(i);
                DataPoint normalizedPoint = point.normalize();
                normalizedBatch.add(normalizedPoint);
                
                // Report progress periodically
                if ((i + 1) % 100 == 0 && progressListener != null) {
                    progressListener.onProgressUpdate(i + 1, totalSize, 
                                                     "Normalized " + (i + 1) + " data points");
                }
            }
            
            // Add the normalized batch to the target list
            targetList.addAll(normalizedBatch);
            
            // Report memory usage after each batch
            reportMemoryUsage();
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onProgressUpdate(totalSize, totalSize, 
                                             "Completed normalizing " + totalSize + " data points");
            progressListener.onOperationComplete("Data normalization", true, 
                                                "Successfully normalized " + totalSize + " data points");
        }
    }

    /**
     * Generates a grid of neurons based on the random weight vectors.
     * This method creates a 2D grid of neurons, where each neuron is initialized with
     * a weight vector from the random training set. The grid dimensions are based on
     * the size of the random training set, with each row containing 10 neurons.
     */
    public void generateNeuronGrid() {
        if (this.randomTrainingSet == null || this.randomTrainingSet.isEmpty()) {
            throw new IllegalStateException("Random training set must be created first");
        }
        
        int rows = this.randomTrainingSet.size() / 10;
        this.neuronGrid = new Neuron[rows][10];
        
        // Report memory usage before grid generation
        reportMemoryUsage();
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Generating neuron grid", rows * 10);
        }
        
        System.out.println("Generating neuron grid...");
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 10; col++) {
                Neuron neuron = new Neuron(this.randomTrainingSet.get(index), 0.0, row, col);
                index++;
                this.neuronGrid[row][col] = neuron;
                
                // Report progress periodically
                if (index % 10 == 0 && progressListener != null) {
                    progressListener.onProgressUpdate(index, rows * 10, 
                                                     "Generated " + index + " neurons");
                }
            }
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onProgressUpdate(rows * 10, rows * 10, 
                                             "Completed generating " + (rows * 10) + " neurons");
            progressListener.onOperationComplete("Neuron grid generation", true, 
                                                "Successfully generated " + (rows * 10) + " neurons");
        }
        
        // Report memory usage after grid generation
        reportMemoryUsage();
    }
    
    /**
     * Calculates the Euclidean distance between the given data point and all neurons in the grid.
     * This method supports parallel processing for better performance with large datasets.
     *
     * @param dataPoint The data point to compare against all neurons
     */
    public void calculateDistances(DataPoint dataPoint) {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        if (dataPoint == null) {
            throw new IllegalArgumentException("Data point cannot be null");
        }
        
        // Get the total number of neurons for progress reporting
        int totalNeurons = this.neuronGrid.length * this.neuronGrid[0].length;
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Calculating distances", totalNeurons);
        }
        
        // Calculate distances in parallel if the grid is large enough
        if (totalNeurons > 1000) {
            calculateDistancesParallel(dataPoint);
        } else {
            calculateDistancesSequential(dataPoint);
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onOperationComplete("Distance calculation", true, 
                                                "Successfully calculated distances for " + totalNeurons + " neurons");
        }
    }
    
    /**
     * Calculates distances sequentially for small grids.
     *
     * @param dataPoint The data point to compare against all neurons
     */
    private void calculateDistancesSequential(DataPoint dataPoint) {
        int totalNeurons = this.neuronGrid.length * this.neuronGrid[0].length;
        int processedCount = 0;
        
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                neuron.calculateDistance(dataPoint);
                processedCount++;
                
                // Report progress periodically
                if (processedCount % 100 == 0 && progressListener != null) {
                    progressListener.onProgressUpdate(processedCount, totalNeurons, 
                                                     "Calculated " + processedCount + " distances");
                }
            }
        }
    }
    
    /**
     * Calculates distances in parallel for large grids.
     *
     * @param dataPoint The data point to compare against all neurons
     */
    private void calculateDistancesParallel(DataPoint dataPoint) {
        // Use parallel streams to calculate distances
        Arrays.stream(this.neuronGrid)
              .parallel()
              .forEach(row -> {
                  for (Neuron neuron : row) {
                      neuron.calculateDistance(dataPoint);
                  }
              });
        
        // Note: Progress reporting is not used in parallel mode as it would require
        // synchronization which could impact performance
    }
    
    /**
     * Finds the neurons with the smallest distance in the grid (Best Matching Units).
     * This method identifies the neuron(s) with the minimum distance to the input vector
     * and adds them to the bestMatchingUnits list.
     * 
     * @return The list of best matching neurons
     */
    public List<Neuron> findBestMatchingUnits() {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        
        // Initialize the list of best matching units
        this.bestMatchingUnits = new ArrayList<>();
        
        // Find the neuron with the minimum distance
        Neuron minNeuron = null;
        double minDistance = Double.MAX_VALUE;
        
        // Notify progress listener that operation is starting
        int totalNeurons = this.neuronGrid.length * this.neuronGrid[0].length;
        if (progressListener != null) {
            progressListener.onOperationStart("Finding best matching units", totalNeurons);
        }
        
        // First pass: find the minimum distance
        int processedCount = 0;
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                if (neuron.getDistance() < minDistance) {
                    minDistance = neuron.getDistance();
                    minNeuron = neuron;
                }
                
                processedCount++;
                // Report progress periodically
                if (processedCount % 100 == 0 && progressListener != null) {
                    progressListener.onProgressUpdate(processedCount, totalNeurons, 
                                                     "Processed " + processedCount + " neurons");
                }
            }
        }
        
        // Add the neuron with the minimum distance to the list
        if (minNeuron != null) {
            this.bestMatchingUnits.add(minNeuron);
            
            // Second pass: find any other neurons with the same minimum distance
            processedCount = 0;
            for (Neuron[] row : this.neuronGrid) {
                for (Neuron neuron : row) {
                    if (neuron != minNeuron && Math.abs(neuron.getDistance() - minDistance) < 1e-10) {
                        this.bestMatchingUnits.add(neuron);
                    }
                    
                    processedCount++;
                    // Report progress periodically
                    if (processedCount % 100 == 0 && progressListener != null) {
                        progressListener.onProgressUpdate(processedCount, totalNeurons, 
                                                         "Finding additional BMUs: " + processedCount + " neurons");
                    }
                }
            }
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onOperationComplete("BMU finding", true, 
                                                "Found " + this.bestMatchingUnits.size() + " best matching units");
        }
        
        return this.bestMatchingUnits;
    }
    
    /**
     * Returns a random index from the list of best matching units.
     * This is useful when multiple neurons have the same minimum distance.
     *
     * @return A randomly selected index from the best matching units list
     * @throws IllegalStateException if the best matching units list is empty
     */
    public int getRandomBMUIndex() {
        if (this.bestMatchingUnits == null || this.bestMatchingUnits.isEmpty()) {
            throw new IllegalStateException("Best matching units list is empty");
        }
        
        return random.nextInt(this.bestMatchingUnits.size());
    }
    
    /**
     * Calculates the neighborhood of neurons around a center neuron.
     * This method identifies all neurons within a specified radius of the center neuron
     * and stores them in the neighborhoodGrid field.
     *
     * @param centerNeuron The neuron at the center of the neighborhood
     * @param radius The radius of the neighborhood (Manhattan distance)
     * @return A 2D array containing the neurons in the neighborhood
     */
    public Neuron[][] calculateNeighborhood(Neuron centerNeuron, int radius) {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        if (centerNeuron == null) {
            throw new IllegalArgumentException("Center neuron cannot be null");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Calculating neighborhood", 1);
        }
        
        // Collect all neurons within the radius
        List<Neuron> neighborhood = new ArrayList<>();
        
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                if (neuron.isNeighbor(centerNeuron, radius)) {
                    neighborhood.add(neuron);
                }
            }
        }
        
        // If neighborhood is empty, just return the center neuron
        if (neighborhood.isEmpty()) {
            neighborhood.add(centerNeuron);
        }
        
        // Find the bounds of the neighborhood
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (Neuron neuron : neighborhood) {
            minX = Math.min(minX, neuron.getXPosition());
            maxX = Math.max(maxX, neuron.getXPosition());
            minY = Math.min(minY, neuron.getYPosition());
            maxY = Math.max(maxY, neuron.getYPosition());
        }
        
        // Create the neighborhood grid
        int rows = maxX - minX + 1;
        int cols = maxY - minY + 1;
        this.neighborhoodGrid = new Neuron[rows][cols];
        
        // Fill the neighborhood grid
        for (Neuron neuron : neighborhood) {
            int relativeX = neuron.getXPosition() - minX;
            int relativeY = neuron.getYPosition() - minY;
            this.neighborhoodGrid[relativeX][relativeY] = neuron;
        }
        
        // Report progress
        if (progressListener != null) {
            progressListener.onProgressUpdate(1, 1, "Neighborhood calculated with " + neighborhood.size() + " neurons");
            progressListener.onOperationComplete("Neighborhood calculation", true, 
                                                "Neighborhood size: " + neighborhood.size() + " neurons");
        }
        
        return this.neighborhoodGrid;
    }

    /**
     * Trains the SOM using competitive learning.
     * This method adjusts the weights of neurons in the grid based on their
     * proximity to input vectors, gradually converging the map to represent
     * the input data distribution.
     *
     * @param useRandomOrder Whether to use randomized data order (true) or sequential order (false)
     */
    public void trainSOM(boolean useRandomOrder) {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        if (this.normalizedDataPoints == null || this.normalizedDataPoints.isEmpty()) {
            throw new IllegalStateException("Normalized data points must be available");
        }
        
        // Initialize training parameters
        double initialLearningRate = 0.7;
        double finalLearningRate = 0.07;
        double learningRate = initialLearningRate;
        
        int initialRadius = 3;
        int finalRadius = 1;
        int radius = initialRadius;
        
        // Calculate total iterations
        int totalIterations = 5 * this.normalizedDataPoints.size();
        int phase1Iterations = (int)(0.2 * totalIterations);
        
        // Report memory usage before training
        reportMemoryUsage();
        
        // Notify progress listener that operation is starting
        if (progressListener != null) {
            progressListener.onOperationStart("Training SOM", totalIterations);
        }
        
        System.out.println("Training SOM with " + totalIterations + " iterations...");
        
        // Main training loop
        for (int iteration = 0; iteration < totalIterations; iteration++) {
            // Process each data point
            for (int dataIndex = 0; dataIndex < this.normalizedDataPoints.size(); dataIndex++) {
                // Get the current data point (random or sequential)
                DataPoint currentPoint;
                if (useRandomOrder) {
                    currentPoint = this.normalizedDataPoints.get(this.shuffledIndices[dataIndex % this.shuffledIndices.length]);
                } else {
                    currentPoint = this.normalizedDataPoints.get(dataIndex);
                }
                
                // Calculate distances to all neurons
                calculateDistances(currentPoint);
                
                // Find the best matching unit (BMU)
                findBestMatchingUnits();
                int bmuIndex = getRandomBMUIndex();
                Neuron bmu = this.bestMatchingUnits.get(bmuIndex);
                
                // Determine neighborhood radius based on iteration
                if (iteration < phase1Iterations) {
                    if (iteration < phase1Iterations / 3) {
                        radius = initialRadius;
                    } else if (iteration < 2 * (phase1Iterations / 3)) {
                        radius = 2;
                    } else {
                        radius = finalRadius;
                    }
                } else {
                    radius = finalRadius;
                }
                
                // Calculate neighborhood
                calculateNeighborhood(bmu, radius);
                
                // Update weights of neurons in the neighborhood
                for (Neuron[] row : this.neighborhoodGrid) {
                    for (Neuron neuron : row) {
                        if (neuron != null) {
                            neuron.updateWeights(currentPoint, learningRate);
                        }
                    }
                }
            }
            
            // Decrease learning rate
            if (iteration < totalIterations - 1) {
                double progress = (double)iteration / totalIterations;
                learningRate = initialLearningRate * (1 - progress);
                
                // Ensure learning rate doesn't go below minimum
                if (learningRate < finalLearningRate) {
                    learningRate = finalLearningRate;
                }
            }
            
            // Report progress periodically
            if ((iteration + 1) % 10 == 0 && progressListener != null) {
                progressListener.onProgressUpdate(iteration + 1, totalIterations, 
                                                 "Completed " + (iteration + 1) + " iterations");
                
                // Report memory usage periodically
                if ((iteration + 1) % 100 == 0) {
                    reportMemoryUsage();
                }
            }
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onProgressUpdate(totalIterations, totalIterations, 
                                             "Completed " + totalIterations + " iterations");
            progressListener.onOperationComplete("SOM training", true, 
                                                "Successfully trained SOM with " + totalIterations + " iterations");
        }
        
        System.out.println("SOM training completed.");
    }

    /**
     * Assigns class labels to neurons based on their proximity to data points.
     * This method calculates the Euclidean distance between each neuron and all
     * data points, then assigns the label of the closest data point to the neuron.
     */
    public void assignClassLabels() {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        if (this.dataPoints == null || this.dataPoints.isEmpty()) {
            throw new IllegalStateException("Data points must be available");
        }
        
        // Report memory usage before assignment
        reportMemoryUsage();
        
        // Notify progress listener that operation is starting
        int totalNeurons = this.neuronGrid.length * this.neuronGrid[0].length;
        if (progressListener != null) {
            progressListener.onOperationStart("Assigning class labels", totalNeurons);
        }
        
        System.out.println("Assigning class labels to neurons...");
        
        // Process each neuron
        int processedCount = 0;
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                // Calculate distances to all data points
                double[] distances = new double[this.dataPoints.size()];
                
                for (int i = 0; i < this.dataPoints.size(); i++) {
                    distances[i] = neuron.getWeightVector().euclideanDistance(this.dataPoints.get(i));
                }
                
                // Find the closest data point
                int closestIndex = findMinimumIndex(distances);
                
                // Assign the label from the original data point
                neuron.setClassLabel(this.dataPoints.get(closestIndex).getLabel());
                
                processedCount++;
                // Report progress periodically
                if (processedCount % 10 == 0 && progressListener != null) {
                    progressListener.onProgressUpdate(processedCount, totalNeurons, 
                                                     "Assigned " + processedCount + " labels");
                }
            }
        }
        
        // Report final progress
        if (progressListener != null) {
            progressListener.onProgressUpdate(totalNeurons, totalNeurons, 
                                             "Completed assigning " + totalNeurons + " labels");
            progressListener.onOperationComplete("Class label assignment", true, 
                                                "Successfully assigned labels to " + totalNeurons + " neurons");
        }
    }
    
    /**
     * Finds the index of the minimum value in an array.
     *
     * @param values The array to search
     * @return The index of the minimum value
     */
    private int findMinimumIndex(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values array cannot be null or empty");
        }
        
        double min = values[0];
        int minIndex = 0;
        
        for (int i = 1; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
                minIndex = i;
            }
        }
        
        return minIndex;
    }

    /**
     * Renames class labels to single characters for better visualization.
     * This method assigns a unique character (starting from 'a') to each
     * unique class label in the grid and updates the neurons accordingly.
     */
    public void renameClassLabels() {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        
        // Collect all unique labels
        List<String> uniqueLabels = new ArrayList<>();
        
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                String label = neuron.getClassLabel();
                if (!label.isEmpty() && !uniqueLabels.contains(label)) {
                    uniqueLabels.add(label);
                }
            }
        }
        
        // Print the mapping
        for (int i = 0; i < uniqueLabels.size(); i++) {
            char newLabel = (char)('a' + i);
            System.out.print(uniqueLabels.get(i) + " = " + newLabel + "\t");
        }
        System.out.println();
        
        // Rename labels in the grid
        for (int i = 0; i < uniqueLabels.size(); i++) {
            String oldLabel = uniqueLabels.get(i);
            String newLabel = Character.toString((char)('a' + i));
            
            for (Neuron[] row : this.neuronGrid) {
                for (Neuron neuron : row) {
                    if (oldLabel.equals(neuron.getClassLabel())) {
                        neuron.setClassLabel(newLabel);
                    }
                }
            }
        }
    }

    /**
     * Prints the class labels of neurons in the grid.
     * This method displays the grid of neurons with their assigned class labels.
     */
    public void printClassLabels() {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                System.out.print(neuron.toString() + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Counts and prints the occurrences of each class label in the grid.
     * This method counts how many neurons are assigned to each class and
     * displays the results.
     */
    public void countClassLabels() {
        if (this.neuronGrid == null) {
            throw new IllegalStateException("Neuron grid must be generated first");
        }
        
        // Collect all unique labels
        List<String> uniqueLabels = new ArrayList<>();
        
        for (Neuron[] row : this.neuronGrid) {
            for (Neuron neuron : row) {
                String label = neuron.getClassLabel();
                if (!label.isEmpty() && !uniqueLabels.contains(label)) {
                    uniqueLabels.add(label);
                }
            }
        }
        
        // Count occurrences of each label
        int[] counts = new int[uniqueLabels.size()];
        
        for (int i = 0; i < uniqueLabels.size(); i++) {
            String label = uniqueLabels.get(i);
            
            for (Neuron[] row : this.neuronGrid) {
                for (Neuron neuron : row) {
                    if (label.equals(neuron.getClassLabel())) {
                        counts[i]++;
                    }
                }
            }
        }
        
        // Print the counts
        for (int i = 0; i < uniqueLabels.size(); i++) {
            System.out.print(uniqueLabels.get(i) + " = " + counts[i] + "\t");
        }
        System.out.println();
    }
}