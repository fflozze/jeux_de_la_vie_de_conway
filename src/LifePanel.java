import javax.swing.*; // Importe les classes Swing (JComponent, JPanel, JFrame, etc.)
import java.awt.*; // Importe AWT pour les couleurs, graphiques, dimensions, curseurs
import java.awt.event.MouseAdapter; // Importe l'adaptateur de souris pour gérer les événements de souris
import java.awt.event.MouseEvent; // Importe la classe d'événement de souris
import java.awt.event.ActionEvent; // Importe la classe d'événement d'action (utilisée par AbstractAction)

public class LifePanel extends JPanel { // Panneau qui affiche la grille et gère les interactions utilisateur
    private final GameOfLife life; // Référence au modèle (logique du Jeu de la vie)

    // État du dessin
    private boolean drawing = false; // Indique si l'utilisateur est en train de dessiner/effacer
    private boolean drawState = true; // true = dessiner des cellules vivantes, false = effacer (mortes)

    // État d'affichage/déplacement pour une grille « infinie »
    private int cellSize = 20; // Taille d'une cellule en pixels
    private int offsetRow = 0; // Rangée (ligne) du monde visible en haut à gauche
    private int offsetCol = 0; // Colonne du monde visible en haut à gauche
    private double pixelOffsetX = 0; // Décalage horizontal fin en pixels pour un défilement fluide
    private double pixelOffsetY = 0; // Décalage vertical fin en pixels pour un défilement fluide

    private boolean panning = false; // Indique si on est en mode « déplacement » de la vue
    private Point lastMouse = null; // Dernière position de la souris pour calculer le déplacement

    public LifePanel(GameOfLife life) { // Constructeur: initialise les propriétés du panneau
        this.life = life; // Stocke la référence du modèle
        setBackground(Color.WHITE); // Définit la couleur de fond du panneau
        setPreferredSize(new Dimension(800, 600)); // Taille préférée de la zone d'affichage
        setFocusable(true); // Permet au panneau de recevoir le focus clavier
        setupKeyBindings(); // Configure les raccourcis clavier pour le déplacement
        
        MouseAdapter mouse = new MouseAdapter() { // Crée un adaptateur de souris pour gérer clics/drag
            @Override
            public void mousePressed(MouseEvent e) { // Appelé quand un bouton de la souris est pressé
                requestFocusInWindow(); // Demande le focus pour capter les touches
                if (SwingUtilities.isMiddleMouseButton(e) || e.isAltDown()) { // Si bouton du milieu ou Alt enfoncé
                    // Démarrer le déplacement (panning)
                    panning = true; // Active le mode déplacement
                    lastMouse = e.getPoint(); // Mémorise la position initiale de la souris
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); // Change le curseur en « déplacement »
                } else { // Sinon, on passe en mode dessin/effacement
                    // Démarrer le dessin/effacement
                    drawing = true; // Active le mode dessin
                    drawState = SwingUtilities.isLeftMouseButton(e); // Bouton gauche = dessiner, droit = effacer
                    applyAt(e.getX(), e.getY()); // Applique immédiatement à la position cliquée
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) { // Appelé lors du déplacement de la souris avec bouton enfoncé
                if (panning) { // Si on déplace la vue
                    if (lastMouse == null) { lastMouse = e.getPoint(); return; } // Sécurité si pas encore de position
                    int dx = e.getX() - lastMouse.x; // Déplacement horizontal de la souris
                    int dy = e.getY() - lastMouse.y; // Déplacement vertical de la souris
                    panByPixels(-dx, -dy); // Fait défiler le contenu dans le même sens que le drag
                    lastMouse = e.getPoint(); // Met à jour la dernière position de la souris
                } else if (drawing) { // Sinon si on dessine
                    applyAt(e.getX(), e.getY()); // Applique l'action (dessin/effacement) à la position actuelle
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { // Appelé quand le bouton de la souris est relâché
                if (panning) { // Si on était en mode déplacement
                    panning = false; // Désactive le mode déplacement
                    lastMouse = null; // Oublie la dernière position
                    setCursor(Cursor.getDefaultCursor()); // Restaure le curseur par défaut
                }
                drawing = false; // On arrête le dessin dans tous les cas
            }
        }; // Fin de la classe anonyme MouseAdapter
        addMouseListener(mouse); // Enregistre l'écouteur de clics
        addMouseMotionListener(mouse); // Enregistre l'écouteur de déplacements de la souris
    }

    private void panByPixels(int dx, int dy) { // Fait défiler la vue de dx, dy pixels
        pixelOffsetX += dx; // Met à jour le décalage horizontal fin
        pixelOffsetY += dy; // Met à jour le décalage vertical fin
        // Normalise les décalages en les ramenant dans [0, cellSize) et ajuste les offsets de cellule
        while (pixelOffsetX >= cellSize) { pixelOffsetX -= cellSize; offsetCol++; } // Décale d'une colonne vers la droite
        while (pixelOffsetX < 0) { pixelOffsetX += cellSize; offsetCol--; } // Décale d'une colonne vers la gauche
        while (pixelOffsetY >= cellSize) { pixelOffsetY -= cellSize; offsetRow++; } // Décale d'une ligne vers le bas
        while (pixelOffsetY < 0) { pixelOffsetY += cellSize; offsetRow--; } // Décale d'une ligne vers le haut
        repaint(); // Redessine le panneau pour refléter la nouvelle vue
    }

    private void panByCells(int dCols, int dRows) { // Fait défiler la vue par un nombre entier de cellules
        offsetCol += dCols; // Ajuste l'offset de colonnes
        offsetRow += dRows; // Ajuste l'offset de lignes
        repaint(); // Redessine la vue
    }

    private void setupKeyBindings() { // Configure les raccourcis clavier pour déplacer la vue
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); // Map des touches actives quand la fenêtre a le focus
        ActionMap am = getActionMap(); // Map des actions associées aux noms symboliques

        im.put(KeyStroke.getKeyStroke("LEFT"), "panLeft"); // Associe la flèche gauche à l'action panLeft
        am.put("panLeft", new AbstractAction() { // Déclare l'action panLeft
            @Override public void actionPerformed(ActionEvent e) { panByCells(-1, 0); } // Déplacement d'une colonne à gauche
        });
        im.put(KeyStroke.getKeyStroke("RIGHT"), "panRight"); // Associe la flèche droite à panRight
        am.put("panRight", new AbstractAction() { // Déclare l'action panRight
            @Override public void actionPerformed(ActionEvent e) { panByCells(1, 0); } // Déplacement d'une colonne à droite
        });
        im.put(KeyStroke.getKeyStroke("UP"), "panUp"); // Flèche haut -> panUp
        am.put("panUp", new AbstractAction() { // Action panUp
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, -1); } // Monte d'une ligne
        });
        im.put(KeyStroke.getKeyStroke("DOWN"), "panDown"); // Flèche bas -> panDown
        am.put("panDown", new AbstractAction() { // Action panDown
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, 1); } // Descend d'une ligne
        });

        im.put(KeyStroke.getKeyStroke("shift LEFT"), "panLeftFast"); // Maj+Gauche pour déplacement rapide
        am.put("panLeftFast", new AbstractAction() { // Action panLeftFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(-10, 0); } // 10 colonnes à gauche
        });
        im.put(KeyStroke.getKeyStroke("shift RIGHT"), "panRightFast"); // Maj+Droite -> rapide
        am.put("panRightFast", new AbstractAction() { // Action panRightFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(10, 0); } // 10 colonnes à droite
        });
        im.put(KeyStroke.getKeyStroke("shift UP"), "panUpFast"); // Maj+Haut -> rapide
        am.put("panUpFast", new AbstractAction() { // Action panUpFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, -10); } // 10 lignes vers le haut
        });
        im.put(KeyStroke.getKeyStroke("shift DOWN"), "panDownFast"); // Maj+Bas -> rapide
        am.put("panDownFast", new AbstractAction() { // Action panDownFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, 10); } // 10 lignes vers le bas
        });

        im.put(KeyStroke.getKeyStroke("pressed A"), "panLeftA"); // Touche A -> gauche (alternative de jeu)
        am.put("panLeftA", new AbstractAction() { // Action panLeftA
            @Override public void actionPerformed(ActionEvent e) { panByCells(-1, 0); } // 1 colonne à gauche
        });
        im.put(KeyStroke.getKeyStroke("pressed D"), "panRightD"); // Touche D -> droite
        am.put("panRightD", new AbstractAction() { // Action panRightD
            @Override public void actionPerformed(ActionEvent e) { panByCells(1, 0); } // 1 colonne à droite
        });
        im.put(KeyStroke.getKeyStroke("pressed W"), "panUpW"); // Touche W -> haut
        am.put("panUpW", new AbstractAction() { // Action panUpW
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, -1); } // 1 ligne en haut
        });
        im.put(KeyStroke.getKeyStroke("pressed S"), "panDownS"); // Touche S -> bas
        am.put("panDownS", new AbstractAction() { // Action panDownS
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, 1); } // 1 ligne en bas
        });
        im.put(KeyStroke.getKeyStroke("shift pressed A"), "panLeftAFast"); // Maj+A -> gauche rapide
        am.put("panLeftAFast", new AbstractAction() { // Action panLeftAFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(-10, 0); } // 10 colonnes à gauche
        });
        im.put(KeyStroke.getKeyStroke("shift pressed D"), "panRightDFast"); // Maj+D -> droite rapide
        am.put("panRightDFast", new AbstractAction() { // Action panRightDFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(10, 0); } // 10 colonnes à droite
        });
        im.put(KeyStroke.getKeyStroke("shift pressed W"), "panUpWFast"); // Maj+W -> haut rapide
        am.put("panUpWFast", new AbstractAction() { // Action panUpWFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, -10); } // 10 lignes en haut
        });
        im.put(KeyStroke.getKeyStroke("shift pressed S"), "panDownSFast"); // Maj+S -> bas rapide
        am.put("panDownSFast", new AbstractAction() { // Action panDownSFast
            @Override public void actionPerformed(ActionEvent e) { panByCells(0, 10); } // 10 lignes en bas
        });
    } // Fin de la configuration des raccourcis

    private void applyAt(int x, int y) { // Applique l'action courante (dessin/effacement) à la cellule sous (x,y)
        int worldCol = offsetCol + (int) Math.floor((x + pixelOffsetX) / cellSize); // Convertit x pixel -> colonne du monde
        int worldRow = offsetRow + (int) Math.floor((y + pixelOffsetY) / cellSize); // Convertit y pixel -> ligne du monde
        life.setAlive(worldRow, worldCol, drawState); // Met à jour l'état de la cellule dans le modèle
        repaint(); // Redessine après modification
    }

    @Override
    protected void paintComponent(Graphics g) { // Dessine le contenu du panneau
        super.paintComponent(g); // Nettoie le fond et prépare le contexte
        Graphics2D g2 = (Graphics2D) g.create(); // Crée un contexte graphique 2D isolé
        try { // Bloc try/finally pour garantir la libération des ressources graphiques
            int w = getWidth(); // Largeur visible du panneau en pixels
            int h = getHeight(); // Hauteur visible du panneau en pixels

            // Dessine les cellules vivantes dans la zone visible
            g2.setColor(new Color(32, 136, 203)); // Couleur des cellules vivantes
            int colsVisible = w / cellSize + 2; // Nombre de colonnes visibles (marge de 2 pour le défilement)
            int rowsVisible = h / cellSize + 2; // Nombre de lignes visibles (marge de 2)
            int startX = (int) -Math.floor(pixelOffsetX); // Décalage de départ X en pixels (prend en compte le glissement fin)
            int startY = (int) -Math.floor(pixelOffsetY); // Décalage de départ Y en pixels

            for (int r = 0; r < rowsVisible; r++) { // Parcourt toutes les lignes visibles
                int worldRow = offsetRow + r; // Calcule la ligne du monde correspondant
                int y = startY + r * cellSize; // Position Y du haut de la cellule à dessiner
                for (int c = 0; c < colsVisible; c++) { // Parcourt toutes les colonnes visibles
                    int worldCol = offsetCol + c; // Calcule la colonne du monde correspondante
                    int x = startX + c * cellSize; // Position X de la cellule à dessiner
                    if (life.isAlive(worldRow, worldCol)) { // Si la cellule (monde) est vivante
                        g2.fillRect(x, y, cellSize, cellSize); // Dessine le rectangle plein représentant la cellule
                    }
                }
            }

            // Dessine les lignes de la grille
            g2.setColor(new Color(230, 230, 230)); // Couleur claire pour la grille
            // Lignes verticales
            for (int x = startX; x <= w; x += cellSize) { // Dessine chaque ligne verticale à intervalles de cellSize
                g2.drawLine(x, 0, x, h); // Trace la ligne verticale
            }
            // Lignes horizontales
            for (int y = startY; y <= h; y += cellSize) { // Dessine chaque ligne horizontale à intervalles de cellSize
                g2.drawLine(0, y, w, y); // Trace la ligne horizontale
            }
        } finally { // Toujours exécuté, même si une exception survient
            g2.dispose(); // Libère les ressources du contexte graphique
        }
    } // Fin de paintComponent
} // Fin de la classe LifePanel
