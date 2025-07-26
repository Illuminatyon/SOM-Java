# Self Organizing Map

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![University: Paris 8](https://img.shields.io/badge/University-Paris%208-blue)
![Machine: learning](https://img.shields.io/badge/Machine-Learning-orange)
![Java: 17](https://img.shields.io/badge/Java-17-red)
![Contributors](https://img.shields.io/badge/Contributors-1-brightgreen)
![Stars](https://img.shields.io/badge/Stars-0-lightgrey)
![Fork](https://img.shields.io/badge/Forks-0-lightgrey)
![Watchers](https://img.shields.io/badge/Watchers-0-lightgrey)

## 🌍 Multilingual README Versions

- 🇫🇷 [Français](README.fr.md)
- 🇬🇧 English (you are here)
- 🇪🇸 [Español](README.es.md)

## 📘 Project Overview

This project is a simplified implementation of a Self-Organizing Map (SOM) developed out of my passion for mathematics and my interest in joining a prestigious engineering school. The main objective was to understand the workings of the SOM algorithm by recreating a from scratch version with the following constraints:

- No global variables used.
- BMUs (Best Matching Units) are stored in a linked list.
- The size of collections: matrices and linked lists must be calculated dynamically.

## 📊 Dataset

The dataset used is the Iris dataset, available on Kaggle:
[Iris Dataset](https://www.kaggle.com/datasets/uciml/iris)

- 150 samples
- 4 features per sample:
  - Sepal length
  - Sepal width
  - Petal length
  - Petal width
- 3 classes:
  - Iris-setosa
  - Iris-versicolor
  - Iris-virginica

## ⚙️ SOM Algorithm Functioning

The Self-Organizing Map (SOM) is an unsupervised neural network used for dimensionality reduction and data visualization. Here are the main steps:

1. **Initialization**
   The neurons in the map are randomly initialized in the feature space.

2. **Distance Calculation**
   For each input data, the Euclidean distance between this data and all neurons is calculated.
   The neuron with the smallest distance is called the BMU (Best Matching Unit). This process follows the Winner-Takes-All (WTA) principle, where only the neuron closest to the input data is selected, along with its neighbors.

3. **Map Update**
   The BMU and its neighbors are adjusted to move closer to the input data, according to a learning rate α. This gradually adjusts the map to better represent the input data.

4. **Iteration**
   Steps 2 to 3 are repeated for a set number of iterations.
   - The learning rate decreases over time.
   - The neighborhood size gradually shrinks, allowing the map to specialize while maintaining topological consistency.

The result is a topologically organized map, where similar classes are found in close areas. The SOM algorithm thus groups similar data while preserving their structure.

## 🧑‍💻 Technologies Used

- Language: Java (from scratch implementation)

## 💻 Install Java (If You Don't Have Java Installed)

If you don't have Java installed, you can follow the instructions in one of my YouTube videos for installing Java on different platforms:

- Linux: [Install Java on Linux](https://www.youtube.com/watch?v=example1)
- Mac: [Install Java on Mac](https://www.youtube.com/watch?v=example2)
- Windows: [Install Java on Windows](https://www.youtube.com/watch?v=example3)

## 📝 Compilation and Execution

### Clone the repository

```bash
git clone https://github.com/Fab16BSB/SOM_JAVA.git
cd SOM_JAVA
```

### Using the build script (Windows)

The easiest way to build and run the application is using the provided build script:

```bash
build.bat
```

When prompted "Run the application now? (y/n):", type `y` to run the application immediately.

### Manual compilation

```bash
javac -d bin src/main/java/com/fabio/som/*.java
```

### Running the application

You can run the application in two ways:

#### 1. Standard application

```bash
java -cp bin com.fabio.som.SOMApplication data/test_data.csv
```

#### 2. Visual application (recommended)

```bash
java -cp bin com.fabio.som.SOMVisualizer data/test_data.csv
```

The visualizer provides a more user-friendly interface with clear output formatting and visualization of the SOM results.

## 📊 Visualization Features

The SOMVisualizer provides enhanced visualization capabilities:

1. **Class Mapping**: Shows how original class labels are mapped to simplified characters for better visualization
   ```
   Iris-setosa = a    Iris-versicolor = b    Iris-virginica = c
   ```

2. **Grid Visualization**: Displays the SOM grid with assigned classes, showing how the data is clustered
   ```
   [a]    [a]    [a]    [a]    [b]    [b]    [b]    [c]    [c]    [c]
   [a]    [a]    [a]    [b]    [b]    [b]    [c]    [c]    [c]    [c]
   [a]    [a]    [b]    [b]    [b]    [c]    [c]    [c]    [c]    [c]
   ```

3. **Class Distribution**: Shows the number of neurons assigned to each class
   ```
   a = 10    b = 12    c = 18
   ```

## 📈 Results

When you run the application, you'll see the SOM training process and the resulting classification of the Iris dataset. The output will show:

1. Data loading and normalization
2. Mean vector calculation
3. Random weight vector creation
4. Neuron grid generation
5. SOM training
6. Class label assignment and visualization

The final output will display a grid of neurons with their assigned classes, showing how the SOM has organized the data into clusters.

## Components

The implementation consists of five main classes:

1. **DataPoint**: Represents a multi-dimensional data point with numerical features and an optional label.
2. **Neuron**: Represents a node in the SOM grid, containing a weight vector and position.
3. **SOMProcessor**: Core processing engine that handles data normalization, SOM training, and classification.
4. **SOMApplication**: Main application class providing a command-line interface.
5. **SOMVisualizer**: Enhanced visualization interface for displaying SOM results.

## Implementation Details

### SOM Training Algorithm

The training process follows these steps:

1. Initialize a grid of neurons with random weight vectors
2. For each input vector:
   - Find the Best Matching Unit (BMU) - the neuron with the closest weight vector
   - Update the BMU and its neighbors' weight vectors to move closer to the input vector
3. Gradually reduce the learning rate and neighborhood size
4. Assign class labels to neurons based on the closest input vectors

### Parameters

- **Upper/Lower Bounds**: Control the range for random weight initialization
- **Training Mode**: Sequential (0) or Random (1)
- **Learning Rate**: Starts at 0.7 and decreases to 0.07 during training
- **Neighborhood Radius**: Starts at 3 and decreases to 1 during training

## References

- Kohonen, T. (1982). Self-organized formation of topologically correct feature maps. Biological Cybernetics, 43(1), 59-69.
- Kohonen, T. (1990). The self-organizing map. Proceedings of the IEEE, 78(9), 1464-1480.
