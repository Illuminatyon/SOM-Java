# Self Organizing Map

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Université: Paris 8](https://img.shields.io/badge/Université-Paris%208-blue)
![Machine: learning](https://img.shields.io/badge/Machine-Learning-orange)
![Java: 17](https://img.shields.io/badge/Java-17-red)
![Contributeurs](https://img.shields.io/badge/Contributeurs-1-brightgreen)
![Étoiles](https://img.shields.io/badge/Étoiles-0-lightgrey)
![Forks](https://img.shields.io/badge/Forks-0-lightgrey)
![Observateurs](https://img.shields.io/badge/Observateurs-0-lightgrey)

## 🌍 Versions multilingues du README

- 🇫🇷 Français (vous êtes ici)
- 🇬🇧 [English](README.md)
- 🇪🇸 [Español](README.es.md)

## 📘 Aperçu du projet

Ce projet est une implémentation simplifiée d'une Carte Auto-Organisatrice (SOM) développée par passion pour les mathématiques et par intérêt pour intégrer une grande école d'ingénieur. L'objectif principal était de comprendre le fonctionnement de l'algorithme SOM en recréant une version à partir de zéro avec les contraintes suivantes :

- Aucune variable globale utilisée.
- Les BMU (Best Matching Units) sont stockées dans une liste chaînée.
- La taille des collections : matrices et listes chaînées doivent être calculées dynamiquement.

## 📊 Jeu de données

Le jeu de données utilisé est le jeu de données Iris, disponible sur Kaggle :
[Jeu de données Iris](https://www.kaggle.com/datasets/uciml/iris)

- 150 échantillons
- 4 caractéristiques par échantillon :
  - Longueur du sépale
  - Largeur du sépale
  - Longueur du pétale
  - Largeur du pétale
- 3 classes :
  - Iris-setosa
  - Iris-versicolor
  - Iris-virginica

## ⚙️ Fonctionnement de l'algorithme SOM

La Carte Auto-Organisatrice (SOM) est un réseau de neurones non supervisé utilisé pour la réduction de dimensionnalité et la visualisation de données. Voici les principales étapes :

1. **Initialisation**
   Les neurones de la carte sont initialisés aléatoirement dans l'espace des caractéristiques.

2. **Calcul de distance**
   Pour chaque donnée d'entrée, la distance euclidienne entre cette donnée et tous les neurones est calculée.
   Le neurone avec la plus petite distance est appelé BMU (Best Matching Unit). Ce processus suit le principe du Winner-Takes-All (WTA), où seul le neurone le plus proche de la donnée d'entrée est sélectionné, ainsi que ses voisins.

3. **Mise à jour de la carte**
   Le BMU et ses voisins sont ajustés pour se rapprocher de la donnée d'entrée, selon un taux d'apprentissage α. Cela ajuste progressivement la carte pour mieux représenter les données d'entrée.

4. **Itération**
   Les étapes 2 à 3 sont répétées pour un nombre défini d'itérations.
   - Le taux d'apprentissage diminue avec le temps.
   - La taille du voisinage se réduit progressivement, permettant à la carte de se spécialiser tout en maintenant une cohérence topologique.

Le résultat est une carte organisée topologiquement, où des classes similaires se trouvent dans des zones proches. L'algorithme SOM regroupe ainsi des données similaires tout en préservant leur structure.

## 🧑‍💻 Technologies utilisées

- Langage : Java (implémentation à partir de zéro)

## 💻 Installer Java (si vous n'avez pas Java installé)

Si vous n'avez pas Java installé, vous pouvez suivre les instructions dans l'une de mes vidéos YouTube pour installer Java sur différentes plateformes :

- Linux : [Installer Java sur Linux](https://www.youtube.com/watch?v=example1)
- Mac : [Installer Java sur Mac](https://www.youtube.com/watch?v=example2)
- Windows : [Installer Java sur Windows](https://www.youtube.com/watch?v=example3)

## 📝 Compilation et exécution

### Cloner le dépôt

```bash
git clone https://github.com/Fab16BSB/SOM_JAVA.git
cd SOM_JAVA
```

### Compilation

```bash
javac -d bin src/main/java/com/fabio/som/*.java
```

### Exécution

```bash
java -cp bin com.fabio.som.SOMApplication data/test_data.csv
```

## 📈 Résultats

Lorsque vous exécutez l'application, vous verrez le processus d'entraînement SOM et la classification résultante du jeu de données Iris. La sortie affichera :

1. Chargement et normalisation des données
2. Calcul du vecteur moyen
3. Création de vecteurs de poids aléatoires
4. Génération de la grille de neurones
5. Entraînement SOM
6. Attribution et visualisation des étiquettes de classe

La sortie finale affichera une grille de neurones avec leurs classes assignées, montrant comment le SOM a organisé les données en clusters.

## Composants

L'implémentation se compose de quatre classes principales :

1. **DataPoint** : Représente un point de données multidimensionnel avec des caractéristiques numériques et une étiquette optionnelle.
2. **Neuron** : Représente un nœud dans la grille SOM, contenant un vecteur de poids et une position.
3. **SOMProcessor** : Moteur de traitement principal qui gère la normalisation des données, l'entraînement SOM et la classification.
4. **SOMApplication** : Classe d'application principale fournissant une interface en ligne de commande.

## Détails d'implémentation

### Algorithme d'entraînement SOM

Le processus d'entraînement suit ces étapes :

1. Initialiser une grille de neurones avec des vecteurs de poids aléatoires
2. Pour chaque vecteur d'entrée :
   - Trouver la Best Matching Unit (BMU) - le neurone avec le vecteur de poids le plus proche
   - Mettre à jour les vecteurs de poids du BMU et de ses voisins pour les rapprocher du vecteur d'entrée
3. Réduire progressivement le taux d'apprentissage et la taille du voisinage
4. Attribuer des étiquettes de classe aux neurones en fonction des vecteurs d'entrée les plus proches

### Paramètres

- **Bornes supérieure/inférieure** : Contrôlent la plage d'initialisation aléatoire des poids
- **Mode d'entraînement** : Séquentiel (0) ou Aléatoire (1)
- **Taux d'apprentissage** : Commence à 0,7 et diminue jusqu'à 0,07 pendant l'entraînement
- **Rayon de voisinage** : Commence à 3 et diminue jusqu'à 1 pendant l'entraînement

## Références

- Kohonen, T. (1982). Self-organized formation of topologically correct feature maps. Biological Cybernetics, 43(1), 59-69.
- Kohonen, T. (1990). The self-organizing map. Proceedings of the IEEE, 78(9), 1464-1480.
