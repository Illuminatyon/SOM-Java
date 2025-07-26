package com.fabio.som;

import java.util.Arrays;

/**
 * Represents a multi-dimensional data point with numerical features and an optional label.
 * 
 * This class is used to store and manipulate data points for Self-Organizing Maps (SOM).
 * Each data point consists of a vector of numerical features (dimensions) and an optional
 * label or class identifier. The class provides methods to create data points from various
 * sources, access and modify their features, and perform basic operations.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class DataPoint {

    /** The numerical features/dimensions of this data point */
    private double[] features;

    /** The label or class identifier associated with this data point */
    private String label;

    /**
     * Constructs a DataPoint from an array of strings, where all but the last
     * element are parsed as numerical values, and the last element is used as the label.
     *
     * @param data Array of strings containing the feature values and label
     * @throws NumberFormatException if any of the feature values cannot be parsed as a double
     * @throws IllegalArgumentException if the input array is empty
     */
    public DataPoint(String[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Input data array cannot be null or empty");
        }
        
        features = new double[data.length - 1];
        for (int i = 0; i < data.length; i++) {
            if (i == data.length - 1) {
                label = data[i];
            } else {
                try {
                    features[i] = Double.parseDouble(data[i]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Failed to parse feature at index " + i + ": " + data[i]);
                }
            }
        }
    }
    
    /**
     * Constructs a DataPoint from an array of strings with flexible column mapping.
     * This constructor allows specifying which columns contain features, which column
     * contains the label, and which columns should be skipped (e.g., ID columns).
     *
     * @param data Array of strings containing the data values
     * @param featureIndices Array of indices indicating which columns contain feature values
     * @param labelIndex Index of the column containing the label (-1 if no label)
     * @param defaultValue Default value to use for missing or unparseable features
     * @throws IllegalArgumentException if data is null or empty, or if indices are invalid
     */
    public DataPoint(String[] data, int[] featureIndices, int labelIndex, double defaultValue) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Input data array cannot be null or empty");
        }
        if (featureIndices == null || featureIndices.length == 0) {
            throw new IllegalArgumentException("Feature indices array cannot be null or empty");
        }
        
        // Validate indices
        for (int index : featureIndices) {
            if (index < 0 || index >= data.length) {
                throw new IllegalArgumentException("Feature index out of bounds: " + index);
            }
        }
        if (labelIndex >= data.length) {
            throw new IllegalArgumentException("Label index out of bounds: " + labelIndex);
        }
        
        // Initialize features array
        features = new double[featureIndices.length];
        
        // Parse features
        for (int i = 0; i < featureIndices.length; i++) {
            int dataIndex = featureIndices[i];
            try {
                features[i] = Double.parseDouble(data[dataIndex]);
            } catch (NumberFormatException e) {
                // Use default value for unparseable features
                features[i] = defaultValue;
            }
        }
        
        // Parse label if specified
        if (labelIndex >= 0) {
            label = data[labelIndex];
        } else {
            label = "";
        }
    }

    /**
     * Constructs a DataPoint with random feature values within specified minimum and maximum bounds.
     *
     * @param dimensions Number of dimensions/features for this data point
     * @param minValues Minimum values for each dimension
     * @param maxValues Maximum values for each dimension
     * @throws IllegalArgumentException if dimensions is negative or min/max arrays don't match dimensions
     */
    public DataPoint(int dimensions, double[] minValues, double[] maxValues) {
        if (dimensions <= 0) {
            throw new IllegalArgumentException("Number of dimensions must be positive");
        }
        if (minValues == null || maxValues == null || minValues.length != dimensions || maxValues.length != dimensions) {
            throw new IllegalArgumentException("Min and max value arrays must match the specified dimensions");
        }
        
        label = "";
        features = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            if (minValues[i] > maxValues[i]) {
                throw new IllegalArgumentException("Min value must be less than or equal to max value for dimension " + i);
            }
            features[i] = minValues[i] + Math.random() * (maxValues[i] - minValues[i]);
        }
    }

    /**
     * Constructs a DataPoint from a given array of feature values and a label.
     *
     * @param featureValues Array of feature values
     * @param label The label or class identifier for this data point
     * @throws IllegalArgumentException if featureValues is null
     */
    public DataPoint(double[] featureValues, String label) {
        if (featureValues == null) {
            throw new IllegalArgumentException("Feature values array cannot be null");
        }
        
        this.features = Arrays.copyOf(featureValues, featureValues.length);
        this.label = label != null ? label : "";
    }

    /**
     * Returns a copy of the feature values array.
     *
     * @return A copy of the feature values array
     */
    public double[] getFeatures() {
        return Arrays.copyOf(features, features.length);
    }

    /**
     * Returns the label or class identifier of this data point.
     *
     * @return The label associated with this data point
     */
    public String getLabel() {
        return label;
    }

    /**
     * Updates the value of a specific feature dimension.
     *
     * @param value The new value to set
     * @param index The index of the feature to update
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public void setFeature(double value, int index) {
        if (index < 0 || index >= features.length) {
            throw new IndexOutOfBoundsException("Feature index out of bounds: " + index);
        }
        features[index] = value;
    }
    
    /**
     * Sets the label for this data point.
     * 
     * @param label The new label to assign
     */
    public void setLabel(String label) {
        this.label = label != null ? label : "";
    }
    
    /**
     * Returns the number of dimensions/features in this data point.
     * 
     * @return The number of dimensions
     */
    public int getDimensionCount() {
        return features.length;
    }
    
    /**
     * Calculates the Euclidean distance between this data point and another.
     * 
     * @param other The other data point
     * @return The Euclidean distance
     * @throws IllegalArgumentException if the other data point has a different number of dimensions
     */
    public double euclideanDistance(DataPoint other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot calculate distance to null data point");
        }
        if (other.features.length != this.features.length) {
            throw new IllegalArgumentException("Cannot calculate distance between data points with different dimensions");
        }
        
        double sumSquaredDiff = 0.0;
        for (int i = 0; i < features.length; i++) {
            double diff = features[i] - other.features[i];
            sumSquaredDiff += diff * diff;
        }
        return Math.sqrt(sumSquaredDiff);
    }
    
    /**
     * Creates a normalized copy of this data point (unit vector).
     * 
     * @return A new DataPoint with normalized feature values
     */
    public DataPoint normalize() {
        double sumSquared = 0.0;
        for (double value : features) {
            sumSquared += value * value;
        }
        
        double magnitude = Math.sqrt(sumSquared);
        if (magnitude == 0) {
            return new DataPoint(new double[features.length], label);
        }
        
        double[] normalizedFeatures = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            normalizedFeatures[i] = features[i] / magnitude;
        }
        
        return new DataPoint(normalizedFeatures, label);
    }
    
    /**
     * Returns a string representation of this data point.
     * 
     * @return A string containing the feature values and label
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataPoint[features=[");
        for (int i = 0; i < features.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(features[i]);
        }
        sb.append("], label=").append(label).append("]");
        return sb.toString();
    }
}