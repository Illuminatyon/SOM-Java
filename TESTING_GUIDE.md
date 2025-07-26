# Testing Guide for SOM Implementation

This document provides instructions for testing the Self-Organizing Map (SOM) implementation to ensure it works correctly.

## Prerequisites

- Java Development Kit (JDK) 8 or higher installed
- Windows operating system (for using the build.bat script)

## Building and Running the Application

1. Open a command prompt in the project root directory
2. Run the build script:
   ```
   build.bat
   ```
3. When prompted "Run the application now? (y/n):", type `y` and press Enter
4. The application will start with the test data file (data\test_data.csv)

## Test Procedure

Follow these steps to test the application:

1. **Verify Data Loading**
   - The application should display "Data loaded successfully"
   - It should show that there are 30 data points with 4 dimensions
   - A random sample of the data points should be displayed

2. **Mean Vector Calculation**
   - When the application calculates the mean vector, verify that it displays values
   - These values should be between 0 and 1 (normalized)

3. **Parameter Input**
   - When prompted for the upper bound, enter: `0.5`
   - When prompted for the lower bound, enter: `0.5`
   - These values control the initialization range for neuron weights

4. **Training Mode Selection**
   - When prompted for the training mode, enter: `1` (for random order)
   - This will use randomized data order during training

5. **Training Process**
   - The application should display "Training the Self-Organizing Map..."
   - After some processing, it should show "SOM training completed"

6. **Classification Results**
   - The application should display a mapping of class labels to characters
   - It should show a grid of neurons with their assigned classes
   - The grid should show a clear separation between the three Iris classes
   - The class distribution should be displayed

## Expected Results

### Class Mapping
You should see something like:
```
Iris-setosa = a    Iris-versicolor = b    Iris-virginica = c
```

### Neuron Grid
The grid should show a clear clustering pattern, with each class occupying a distinct region:
```
[a] [a] [a] [a] [b] [b] [b] [c] [c] [c]
[a] [a] [a] [b] [b] [b] [c] [c] [c] [c]
[a] [a] [b] [b] [b] [c] [c] [c] [c] [c]
```

### Class Distribution
The distribution should show the number of neurons assigned to each class:
```
a = 10    b = 10    c = 10
```
(Note: The exact numbers may vary depending on the random initialization)

## Interpreting the Results

1. **Clustering Quality**
   - A good result shows clear separation between classes
   - Neurons with the same class should be grouped together
   - There should be a smooth transition between classes

2. **Topological Preservation**
   - Similar data points should be mapped to nearby neurons
   - The transition between classes should follow the actual data similarity
   - For the Iris dataset, versicolor should typically appear between setosa and virginica

3. **Class Distribution**
   - The distribution should roughly reflect the proportion of classes in the input data
   - For our test data, we have 10 samples of each class, so the distribution should be relatively balanced

## Troubleshooting

If you encounter issues:

1. **Compilation Errors**
   - Ensure JDK is properly installed and in your PATH
   - Check for typos or syntax errors in the Java files

2. **Runtime Errors**
   - Verify the test_data.csv file exists in the data directory
   - Ensure the file format is correct (comma-separated values)

3. **Poor Clustering Results**
   - Try different values for upper and lower bounds (e.g., 0.3, 0.7)
   - Run the application multiple times (results vary due to random initialization)
   - Try both training modes (0 for sequential, 1 for random)

## Advanced Testing

For more thorough testing:

1. **Different Datasets**
   - Create or obtain different datasets with varying characteristics
   - Test with datasets of different sizes and dimensionality

2. **Parameter Sensitivity**
   - Test with different upper and lower bounds
   - Compare results between sequential and random training modes

3. **Performance Testing**
   - Test with larger datasets to evaluate performance
   - Measure training time for different dataset sizes