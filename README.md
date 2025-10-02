# Jeu de la vie de Conway – Flozze 🧬

Une simulation moderne et fluide du Jeu de la vie de Conway, développée en Java avec Swing. ⚡

## Description 📋

Ce projet propose une implémentation interactive du Jeu de la vie (Conway's Game of Life). L'application offre une grille virtuelle « infinie » avec déplacement fluide, dessin direct des cellules à la souris, commandes de lecture/pause et réglage de la vitesse. L'objectif est d'explorer des motifs, visualiser leur évolution et expérimenter aisément. 🎨

## Fonctionnalités ✨

- Dessin/effacement des cellules à la souris (clic et glisser). 🖱️
- Déplacement fluide de la vue (panning) à la souris et au clavier. 🧭
- Lecture/Pause de la simulation, pas-à-pas, effacement et remplissage aléatoire. ▶️⏸️⏭️🧹🎲
- Curseur de vitesse (itérations par seconde). ⏱️
- Grille « infinie » avec représentation clairsemée des cellules vivantes. ♾️
- Affichage clair et performant (Swing). 🖼️

## Structure du Projet 📂

jeux_de_la_vie_de_conway\  
│  
├── src\  
│   ├── GameOfLife.java : Logique noyau du Jeu de la vie (monde clairsemé, règles, générations). 🔧  
│   ├── LifePanel.java : Affichage de la grille, dessin/panning, raccourcis clavier. 🎨  
│   ├── ControlPanel.java : Commandes de simulation (boutons, slider, génération). 🎛️  
│   └── Main.java : Point d'entrée de l’application (fenêtre principale). 📦  
│  
├── out\ … : Sorties de compilation (générées). 🏗️  
└── README.md : Documentation du projet. 📖

## Installation 🛠️

Prérequis : Java 8+ (JDK) installé et disponible dans le PATH.

1. Clonez le dépôt sur votre machine locale :

   Powershell
   git clone https://github.com/votre-utilisateur/jeux_de_la_vie_de_conway.git

2. Accédez au répertoire du projet :

   Powershell
   cd jeux_de_la_vie_de_conway

3. Compilez les sources puis lancez l’application :

   Powershell
   javac -d out src\*.java
   java -cp out Main

Astuce IDE: Ouvrez le projet dans IntelliJ IDEA ou Eclipse, marquez le dossier src comme « Source Root », puis exécutez la classe Main.

## Utilisation 💻

Contrôles souris:
- Clic gauche + glisser: dessiner des cellules vivantes. 🟦
- Clic droit + glisser: effacer (rendre mortes). ⬜
- Clic milieu ou touche Alt + glisser: déplacer la vue (panning). 🖐️

Raccourcis clavier (la fenêtre doit avoir le focus):
- Flèches ou W/A/S/D: déplacer la vue d’une cellule.
- Maj + Flèches ou Maj + W/A/S/D: déplacement rapide (10 cellules).

Panneau de contrôle:
- Démarrer/Pause: lance ou met en pause la simulation.
- Pas: avance d’une génération.
- Effacer: vide complètement la grille et réinitialise la génération.
- Aléatoire: remplit une zone centrée avec ~25% de cellules vivantes.
- Vitesse (it/s): règle le nombre d’itérations par seconde.

## Design 🎨

- Interface Swing simple et lisible, grille légère et couleurs sobres.
- Rendu performant avec double-buffer implicite de Swing.
- Fenêtre redimensionnable: la zone visible s’adapte automatiquement.

## Fonctionnalités Techniques 🔧

- Monde clairsemé et potentiellement infini via Set<Long> pour encoder (ligne, colonne).
- Comptage efficace des voisins par dictionnaire (Map<Long, Integer>) à chaque étape.
- Règles de Conway: survie avec 2–3 voisins, naissance avec 3 voisins.
- Panning fluide grâce à la gestion d’offsets en cellules et en pixels.
- Raccourcis clavier configurés via InputMap/ActionMap.
- Pilotage de la simulation par Timer Swing (vitesse ajustable).

## Auteur 👨‍💻

**Flozze**
