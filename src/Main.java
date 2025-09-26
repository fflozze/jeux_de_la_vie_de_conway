import javax.swing.*; // Importe les classes de Swing (fenêtres, composants UI)
import java.awt.*; // Importe les classes AWT (layouts, dimensions, etc.)

public class Main { // Point d'entrée de l'application
    public static void main(String[] args) { // Méthode main, démarrage du programme
        SwingUtilities.invokeLater(() -> { // Programme l'exécution sur le thread d'UI Swing (EDT)
            createAndShowUI(); // Crée et affiche l'interface utilisateur
        }); // Fin du lambda invokeLater
    }

    private static void createAndShowUI() { // Construit la fenêtre principale et les panneaux
        // Configuration de la grille
        int rows = 40; // Nombre de lignes par défaut pour la simulation
        int cols = 60; // Nombre de colonnes par défaut pour la simulation
        GameOfLife life = new GameOfLife(rows, cols); // Crée le modèle du Jeu de la vie

        JFrame frame = new JFrame("Jeu de la vie de Conway"); // Fenêtre principale avec titre
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Quitter l'application à la fermeture de la fenêtre

        LifePanel lifePanel = new LifePanel(life); // Panneau de dessin de la grille et des cellules
        ControlPanel controls = new ControlPanel(life, lifePanel); // Panneau de commandes (boutons, slider, etc.)

        frame.setLayout(new BorderLayout()); // Utilise un BorderLayout pour organiser les panneaux
        frame.add(lifePanel, BorderLayout.CENTER); // Place la grille au centre
        frame.add(controls, BorderLayout.SOUTH); // Place les contrôles en bas

        frame.pack(); // Calcule la taille optimale de la fenêtre en fonction du contenu
        frame.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        frame.setVisible(true); // Affiche la fenêtre
    }
}