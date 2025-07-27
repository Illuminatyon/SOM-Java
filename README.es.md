# Self Organizing Map

![Licencia: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Universidad: Paris 8](https://img.shields.io/badge/Universidad-Paris%208-blue)
![Machine: learning](https://img.shields.io/badge/Machine-Learning-orange)
![Java: 17](https://img.shields.io/badge/Java-17-red)
![Colaboradores](https://img.shields.io/badge/Colaboradores-1-brightgreen)
![Estrellas](https://img.shields.io/badge/Estrellas-0-lightgrey)
![Bifurcaciones](https://img.shields.io/badge/Bifurcaciones-0-lightgrey)
![Observadores](https://img.shields.io/badge/Observadores-0-lightgrey)

## 🌍 Versiones multilingües del README

- 🇫🇷 [Français](README.fr.md)
- 🇬🇧 [English](README.md)
- 🇪🇸 Español (estás aquí)

## 📘 Descripción general del proyecto

Este proyecto es una implementación simplificada de un Mapa Auto-Organizado (SOM) desarrollado por mi pasión por las matemáticas y mi interés en ingresar a una prestigiosa escuela de ingeniería. El objetivo principal era comprender el funcionamiento del algoritmo SOM recreando una versión desde cero con las siguientes restricciones:

- No se utilizan variables globales.
- Las BMUs (Best Matching Units) se almacenan en una lista enlazada.
- El tamaño de las colecciones: matrices y listas enlazadas deben calcularse dinámicamente.

## 📊 Conjunto de datos

El conjunto de datos utilizado es el conjunto de datos Iris, disponible en Kaggle:
[Conjunto de datos Iris](https://www.kaggle.com/datasets/uciml/iris)

- 150 muestras
- 4 características por muestra:
  - Longitud del sépalo
  - Ancho del sépalo
  - Longitud del pétalo
  - Ancho del pétalo
- 3 clases:
  - Iris-setosa
  - Iris-versicolor
  - Iris-virginica

## ⚙️ Funcionamiento del algoritmo SOM

El Mapa Auto-Organizado (SOM) es una red neuronal no supervisada utilizada para la reducción de dimensionalidad y la visualización de datos. Estos son los pasos principales:

1. **Inicialización**
   Las neuronas en el mapa se inicializan aleatoriamente en el espacio de características.

2. **Cálculo de distancia**
   Para cada dato de entrada, se calcula la distancia euclidiana entre este dato y todas las neuronas.
   La neurona con la menor distancia se denomina BMU (Best Matching Unit). Este proceso sigue el principio de Winner-Takes-All (WTA), donde solo se selecciona la neurona más cercana al dato de entrada, junto con sus vecinos.

3. **Actualización del mapa**
   La BMU y sus vecinos se ajustan para acercarse al dato de entrada, según una tasa de aprendizaje α. Esto ajusta gradualmente el mapa para representar mejor los datos de entrada.

4. **Iteración**
   Los pasos 2 a 3 se repiten durante un número determinado de iteraciones.
   - La tasa de aprendizaje disminuye con el tiempo.
   - El tamaño del vecindario se reduce gradualmente, permitiendo que el mapa se especialice mientras mantiene la consistencia topológica.

El resultado es un mapa organizado topológicamente, donde clases similares se encuentran en áreas cercanas. El algoritmo SOM agrupa así datos similares mientras preserva su estructura.

## 🧑‍💻 Tecnologías utilizadas

- Lenguaje: Java (implementación desde cero)

## 💻 Instalar Java (si no tienes Java instalado)

Si no tienes Java instalado, puedes seguir las instrucciones en uno de mis videos de YouTube para instalar Java en diferentes plataformas:

- Linux: [Instalar Java en Linux](https://www.youtube.com/watch?v=QauitHvQZHA)
- Mac: [Instalar Java en Mac](https://www.youtube.com/watch?v=4WKo13f2Qpc)
- Windows: [Instalar Java en Windows](https://www.youtube.com/watch?v=pShVlXCM75I)

## 📝 Compilación y ejecución

### Clonar el repositorio

```bash
git clone https://github.com/Fab16BSB/SOM_JAVA.git
cd SOM_JAVA
```

### Compilación

```bash
javac -d bin src/main/java/com/fabio/som/*.java
```

### Ejecución

```bash
java -cp bin com.fabio.som.SOMApplication data/test_data.csv
```

## 📈 Resultados

Cuando ejecutes la aplicación, verás el proceso de entrenamiento del SOM y la clasificación resultante del conjunto de datos Iris. La salida mostrará:

1. Carga y normalización de datos
2. Cálculo del vector medio
3. Creación de vectores de peso aleatorios
4. Generación de la cuadrícula de neuronas
5. Entrenamiento SOM
6. Asignación y visualización de etiquetas de clase

La salida final mostrará una cuadrícula de neuronas con sus clases asignadas, mostrando cómo el SOM ha organizado los datos en clusters.

## Componentes

La implementación consta de cuatro clases principales:

1. **DataPoint**: Representa un punto de datos multidimensional con características numéricas y una etiqueta opcional.
2. **Neuron**: Representa un nodo en la cuadrícula SOM, que contiene un vector de peso y una posición.
3. **SOMProcessor**: Motor de procesamiento principal que maneja la normalización de datos, el entrenamiento SOM y la clasificación.
4. **SOMApplication**: Clase de aplicación principal que proporciona una interfaz de línea de comandos.

## Detalles de implementación

### Algoritmo de entrenamiento SOM

El proceso de entrenamiento sigue estos pasos:

1. Inicializar una cuadrícula de neuronas con vectores de peso aleatorios
2. Para cada vector de entrada:
   - Encontrar la Best Matching Unit (BMU) - la neurona con el vector de peso más cercano
   - Actualizar los vectores de peso de la BMU y sus vecinos para acercarlos al vector de entrada
3. Reducir gradualmente la tasa de aprendizaje y el tamaño del vecindario
4. Asignar etiquetas de clase a las neuronas basándose en los vectores de entrada más cercanos

### Parámetros

- **Límites superior/inferior**: Controlan el rango para la inicialización aleatoria de pesos
- **Modo de entrenamiento**: Secuencial (0) o Aleatorio (1)
- **Tasa de aprendizaje**: Comienza en 0.7 y disminuye a 0.07 durante el entrenamiento
- **Radio de vecindario**: Comienza en 3 y disminuye a 1 durante el entrenamiento

## Referencias

- Kohonen, T. (1982). Self-organized formation of topologically correct feature maps. Biological Cybernetics, 43(1), 59-69.
- Kohonen, T. (1990). The self-organizing map. Proceedings of the IEEE, 78(9), 1464-1480.
