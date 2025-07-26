package com.fabio.som;

/**
 * Represents a neuron (node) in a Self-Organizing Map (SOM).
 * 
 * Each neuron holds a reference weight vector, its coordinates in the map,
 * the distance to an input vector, and an optional class label. Neurons form
 * the basic building blocks of the SOM grid and are responsible for adapting
 * to the input data during the training process.
 *
 * @author Fabio GUERREIRO MARQUES
 * @version 1.0
 */
public class Neuron {

    /** The weight vector associated with this neuron */
    private DataPoint weightVector;

    /** The class label assigned to this neuron */
    private String classLabel;

    /** The distance between this neuron's weight vector and the current input vector */
    private double distance;

    /** The x-coordinate of this neuron in the SOM grid */
    private int xPosition;

    /** The y-coordinate of this neuron in the SOM grid */
    private int yPosition;

    /**
     * Constructs a neuron with the given weight vector, distance, and coordinates.
     *
     * @param weightVector The reference weight vector for this neuron
     * @param distance The initial distance between this neuron and an input vector
     * @param xPosition The x-coordinate of the neuron in the SOM grid
     * @param yPosition The y-coordinate of the neuron in the SOM grid
     * @throws IllegalArgumentException if weightVector is null
     */
    public Neuron(DataPoint weightVector, double distance, int xPosition, int yPosition) {
        if (weightVector == null) {
            throw new IllegalArgumentException("Weight vector cannot be null");
        }
        
        this.weightVector = weightVector;
        this.classLabel = "";
        this.distance = distance;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * Returns a string representation of the neuron.
     *
     * @return The class label enclosed in brackets
     */
    @Override
    public String toString() {
        return "[" + classLabel + "]";
    }

    /**
     * Returns the distance between this neuron and the current input vector.
     *
     * @return The distance value
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the x-coordinate of the neuron in the SOM grid.
     *
     * @return The x position
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Returns the y-coordinate of the neuron in the SOM grid.
     *
     * @return The y position
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Returns the weight vector associated with this neuron.
     *
     * @return The weight vector
     */
    public DataPoint getWeightVector() {
        return weightVector;
    }

    /**
     * Returns the class label assigned to this neuron.
     *
     * @return The neuron's class label
     */
    public String getClassLabel() {
        return classLabel;
    }

    /**
     * Sets the distance between this neuron and the current input vector.
     *
     * @param distance The new distance value
     * @throws IllegalArgumentException if distance is negative
     */
    public void setDistance(double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.distance = distance;
    }

    /**
     * Sets the class label of the neuron.
     *
     * @param label The new class label to assign
     */
    public void setClassLabel(String label) {
        this.classLabel = label != null ? label : "";
    }
    
    /**
     * Calculates the Euclidean distance between this neuron's weight vector and the given input vector.
     * Updates the neuron's distance value with the calculated result.
     *
     * @param inputVector The input vector to calculate distance to
     * @throws IllegalArgumentException if inputVector is null
     */
    public void calculateDistance(DataPoint inputVector) {
        if (inputVector == null) {
            throw new IllegalArgumentException("Input vector cannot be null");
        }
        
        this.distance = weightVector.euclideanDistance(inputVector);
    }
    
    /**
     * Updates the neuron's weight vector based on the input vector and learning parameters.
     * This implements the SOM learning rule: w(t+1) = w(t) + alpha * (v - w(t))
     * where w is the weight vector, v is the input vector, and alpha is the learning rate.
     *
     * @param inputVector The input vector influencing the update
     * @param learningRate The learning rate (alpha) controlling the magnitude of the update
     * @throws IllegalArgumentException if inputVector is null or has different dimensions
     * @throws IllegalArgumentException if learningRate is outside the range [0,1]
     */
    public void updateWeights(DataPoint inputVector, double learningRate) {
        if (inputVector == null) {
            throw new IllegalArgumentException("Input vector cannot be null");
        }
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        
        double[] currentWeights = weightVector.getFeatures();
        double[] inputFeatures = inputVector.getFeatures();
        
        if (currentWeights.length != inputFeatures.length) {
            throw new IllegalArgumentException("Input vector dimensions do not match weight vector dimensions");
        }
        
        double[] newWeights = new double[currentWeights.length];
        for (int i = 0; i < currentWeights.length; i++) {
            newWeights[i] = currentWeights[i] + learningRate * (inputFeatures[i] - currentWeights[i]);
        }
        
        // Create a new weight vector with the updated weights
        this.weightVector = new DataPoint(newWeights, weightVector.getLabel());
    }
    
    /**
     * Calculates the grid distance between this neuron and another neuron in the SOM grid.
     * This is the Manhattan distance (L1 norm) between the neurons' grid coordinates.
     *
     * @param other The other neuron
     * @return The grid distance
     * @throws IllegalArgumentException if other is null
     */
    public int gridDistance(Neuron other) {
        if (other == null) {
            throw new IllegalArgumentException("Other neuron cannot be null");
        }
        
        return Math.abs(this.xPosition - other.xPosition) + Math.abs(this.yPosition - other.yPosition);
    }
    
    /**
     * Determines if this neuron is a neighbor of another neuron within a specified radius.
     *
     * @param other The other neuron
     * @param radius The neighborhood radius
     * @return true if the neurons are neighbors, false otherwise
     * @throws IllegalArgumentException if other is null or radius is negative
     */
    public boolean isNeighbor(Neuron other, int radius) {
        if (other == null) {
            throw new IllegalArgumentException("Other neuron cannot be null");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        
        return gridDistance(other) <= radius;
    }
}