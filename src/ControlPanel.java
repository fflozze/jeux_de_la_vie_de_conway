import javax.swing.*; // Importe les composants Swing (JPanel, JButton, JLabel, Timer, etc.)
import java.awt.*; // Importe AWT (layout, contraintes, etc.)
import java.awt.event.ActionEvent; // Importe ActionEvent utilisé par les actions et le Timer

public class ControlPanel extends JPanel { // Panneau contenant les contrôles de la simulation
    private final GameOfLife life; // Référence au modèle du Jeu de la vie
    private final LifePanel lifePanel; // Référence au panneau d'affichage pour demander des repaints
    private final Timer timer; // Minuteur pour avancer automatiquement les générations
    private final JButton playPauseBtn = new JButton("Démarrer"); // Bouton démarrer/pause
    private final JButton stepBtn = new JButton("Pas"); // Bouton pour avancer d'un pas
    private final JButton clearBtn = new JButton("Effacer"); // Bouton pour effacer la grille
    private final JButton randomBtn = new JButton("Aléatoire"); // Bouton pour remplir aléatoirement
    private final JSlider speedSlider = new JSlider(1, 60, 10); // Curseur de vitesse (itérations par seconde)
    private final JLabel genLabel = new JLabel("Génération: 0"); // Étiquette affichant le numéro de génération

    public ControlPanel(GameOfLife life, LifePanel lifePanel) { // Constructeur du panneau de contrôle
        this.life = life; // Stocke la référence du modèle
        this.lifePanel = lifePanel; // Stocke la référence du panneau d'affichage
        setLayout(new GridBagLayout()); // Utilise GridBagLayout pour organiser les composants
        GridBagConstraints gc = new GridBagConstraints(); // Contraintes de placement pour GridBagLayout
        gc.insets = new Insets(4,4,4,4); // Marges autour des composants
        gc.gridy = 0; // Ligne initiale pour l'ajout des composants

        int delay = 1000 / speedSlider.getValue(); // Calcule le délai (ms) entre les étapes selon la vitesse
        this.timer = new Timer(delay, (ActionEvent e) -> { // Crée le timer qui avance la simulation
            life.step(); // Passe à la génération suivante
            genLabel.setText("Génération: " + life.getGeneration()); // Met à jour le compteur affiché
            lifePanel.repaint(); // Redessine l'affichage
        }); // Fin de la création du Timer

        playPauseBtn.addActionListener(e -> togglePlay()); // Associe le bouton au basculement démarrer/pause
        stepBtn.addActionListener(e -> { // Action du bouton "Pas"
            if (timer.isRunning()) return; // Empêche l'action si la simulation tourne déjà
            life.step(); // Avance d'une génération
            genLabel.setText("Génération: " + life.getGeneration()); // Met à jour le label de génération
            lifePanel.repaint(); // Redessine
        });
        clearBtn.addActionListener(e -> { // Action du bouton "Effacer"
            life.clear(); // Vide la grille
            genLabel.setText("Génération: 0"); // Réinitialise le compteur affiché
            lifePanel.repaint(); // Redessine
        });
        randomBtn.addActionListener(e -> { // Action du bouton "Aléatoire"
            double density = 0.25; // 25% de cellules vivantes en moyenne
            life.randomize(density); // Remplit aléatoirement la zone par défaut
            genLabel.setText("Génération: 0"); // Réinitialise l'affichage de la génération
            lifePanel.repaint(); // Redessine
        });
        speedSlider.setPaintTicks(true); // Affiche les graduations sur le slider
        speedSlider.setPaintLabels(true); // Affiche les labels de valeurs
        speedSlider.setMajorTickSpacing(10); // Pas majeur de 10
        speedSlider.setMinorTickSpacing(1); // Pas mineur de 1
        speedSlider.addChangeListener(e -> { // Réagit au changement de vitesse
            int sps = speedSlider.getValue(); // Récupère les itérations par seconde souhaitées
            int d = Math.max(1, 1000 / sps); // Convertit en délai (ms) sécurisé (>= 1)
            timer.setDelay(d); // Met à jour le délai du timer
            timer.setInitialDelay(d); // Met à jour le délai initial
        });

        gc.gridx = 0; add(playPauseBtn, gc); // Colonne 0 : bouton démarrer/pause
        gc.gridx = 1; add(stepBtn, gc); // Colonne 1 : bouton pas-à-pas
        gc.gridx = 2; add(clearBtn, gc); // Colonne 2 : bouton effacer
        gc.gridx = 3; add(randomBtn, gc); // Colonne 3 : bouton aléatoire
        gc.gridx = 4; add(new JLabel("Vitesse (it/s):"), gc); // Colonne 4 : étiquette vitesse
        gc.gridx = 5; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL; add(speedSlider, gc); // Colonne 5 : le slider s'étire
        gc.gridx = 6; gc.weightx = 0; gc.fill = GridBagConstraints.NONE; add(genLabel, gc); // Colonne 6 : label de génération
    } // Fin du constructeur

    private void togglePlay() { // Démarre ou met en pause la simulation
        if (timer.isRunning()) { // Si le timer tourne déjà
            timer.stop(); // Met en pause
            playPauseBtn.setText("Démarrer"); // Met à jour le texte du bouton
            setControlsEnabled(true); // Réactive les autres contrôles
        } else { // Sinon, on démarre
            timer.start(); // Lance le timer
            playPauseBtn.setText("Pause"); // Met à jour le texte du bouton
            setControlsEnabled(false); // Désactive les contrôles pour éviter les conflits
            playPauseBtn.setEnabled(true); // Garde le bouton play/pause actif
        }
    }

    private void setControlsEnabled(boolean enabled) { // Active/désactive certains contrôles selon l'état
        stepBtn.setEnabled(enabled); // Active/désactive le bouton pas
        clearBtn.setEnabled(enabled); // Active/désactive le bouton effacer
        randomBtn.setEnabled(enabled); // Active/désactive le bouton aléatoire
        speedSlider.setEnabled(true); // La vitesse reste toujours ajustable
    }
}
