import java.util.*; // Importe toutes les classes utilitaires Java (Collections, Map, Set, Random, etc.)

public class GameOfLife { // Déclaration de la classe principale qui implémente la logique du Jeu de la vie
    // Les lignes/colonnes fournies sont conservées comme zone par défaut pour randomize(),
    // mais le monde lui-même est infini grâce à un ensemble clairsemé des cellules vivantes.
    private final int defaultRows; // Nombre de lignes par défaut utilisé pour la randomisation et l'affichage
    private final int defaultCols; // Nombre de colonnes par défaut utilisé pour la randomisation et l'affichage

    private final Set<Long> alive = new HashSet<>(); // Ensemble des cellules vivantes, encodées en clé long (r,c)
    private long generation = 0; // Compteur de générations écoulées

    public GameOfLife(int rows, int cols) { // Constructeur avec les dimensions par défaut
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("rows/cols must be > 0"); // Validation des paramètres
        this.defaultRows = rows; // Affecte le nombre de lignes par défaut
        this.defaultCols = cols; // Affecte le nombre de colonnes par défaut
    }

    // Accesseurs conservés pour l'IHM ; ils représentent l'étendue par défaut utilisée par randomize()
    public int getRows() { return defaultRows; } // Renvoie le nombre de lignes par défaut
    public int getCols() { return defaultCols; } // Renvoie le nombre de colonnes par défaut
    public long getGeneration() { return generation; } // Renvoie l'index de génération actuel

    public boolean isAlive(int r, int c) { // Indique si la cellule (r,c) est vivante
        return alive.contains(key(r, c)); // Vérifie la présence de la clé (r,c) dans l'ensemble des vivants
    }

    public void setAlive(int r, int c, boolean makeAlive) { // Force l'état (vivant/mort) d'une cellule
        long k = key(r, c); // Calcule la clé unique pour la cellule
        if (makeAlive) alive.add(k); else alive.remove(k); // Ajoute ou supprime la cellule de l'ensemble selon makeAlive
    }

    public void toggle(int r, int c) { // Inverse l'état d'une cellule (vivante <-> morte)
        long k = key(r, c); // Calcule la clé de la cellule
        if (alive.contains(k)) alive.remove(k); else alive.add(k); // Si vivante alors tuer, sinon faire naître
    }

    public void clear() { // Efface tout le monde (toutes les cellules mortes)
        alive.clear(); // Vide l'ensemble des cellules vivantes
        generation = 0; // Réinitialise le compteur de générations
    }

    public void randomize(double density) { // Remplit aléatoirement une zone par défaut avec une densité donnée [0..1]
        if (density < 0) density = 0; // Coupe la densité minimale à 0
        if (density > 1) density = 1; // Coupe la densité maximale à 1
        Random rnd = new Random(); // Générateur aléatoire
        int r0 = -defaultRows / 2; // Décalage d'origine des lignes (centrer la zone autour de 0)
        int c0 = -defaultCols / 2; // Décalage d'origine des colonnes (centrer la zone autour de 0)
        for (int r = 0; r < defaultRows; r++) { // Parcourt chaque ligne de la zone par défaut
            for (int c = 0; c < defaultCols; c++) { // Parcourt chaque colonne de la zone par défaut
                if (rnd.nextDouble() < density) alive.add(key(r0 + r, c0 + c)); // Avec proba=density, rendre la cellule vivante
                else alive.remove(key(r0 + r, c0 + c)); // Sinon s'assurer qu'elle est morte (au cas où)
            }
        }
        generation = 0; // Réinitialise la génération, car on repart d'un nouvel état
    }

    public void step() { // Fait avancer la simulation d'une génération selon les règles de Conway
        // Compte les voisins pour toutes les cellules vivantes et leurs voisines
        Map<Long, Integer> counts = new HashMap<>(); // Dictionnaire: clé cellule -> nombre de voisins vivants
        for (long k : alive) { // Pour chaque cellule vivante actuelle
            int r = (int)(k >> 32); // Récupère la ligne à partir de la clé (bits de poids fort)
            int c = (int)(k & 0xffffffffL); // Récupère la colonne à partir de la clé (bits de poids faible)
            for (int dr = -1; dr <= 1; dr++) { // Parcourt les 3 décalages de ligne (-1,0,1)
                for (int dc = -1; dc <= 1; dc++) { // Parcourt les 3 décalages de colonne (-1,0,1)
                    if (dr == 0 && dc == 0) continue; // Ignore la cellule elle-même
                    long nk = key(r + dr, c + dc); // Clé du voisin candidat
                    counts.put(nk, counts.getOrDefault(nk, 0) + 1); // Incrémente le nombre de voisins vivants pour ce voisin
                }
            }
        }
        Set<Long> next = new HashSet<>(); // Nouvel ensemble de cellules vivantes pour la prochaine génération
        // Les cellules actuellement vivantes survivent avec 2 ou 3 voisins
        for (long k : alive) { // Pour chaque cellule vivante actuelle
            int n = counts.getOrDefault(k, 0); // Nombre de voisins vivants comptés
            if (n == 2 || n == 3) next.add(k); // Survie si 2 ou 3 voisins
        }
        // Les cellules mortes avec exactement 3 voisins deviennent vivantes
        for (Map.Entry<Long, Integer> e : counts.entrySet()) { // Parcourt les cellules ayant au moins un voisin vivant
            if (e.getValue() == 3) next.add(e.getKey()); // Naissance si exactement 3 voisins
        }
        alive.clear(); // Remplace l'état courant
        alive.addAll(next); // Par l'état suivant calculé
        generation++; // Incrémente le compteur de générations
    }

    private static long key(int r, int c) { // Encode une paire (r,c) dans un long pour stockage efficace
        return (((long) r) << 32) ^ (((long) c) & 0xffffffffL); // Combine r (haut 32 bits) et c (bas 32 bits) avec un XOR
    }
} // Fin de la classe GameOfLife
