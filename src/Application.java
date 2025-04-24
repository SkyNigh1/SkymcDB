import javax.swing.*;

/**
 * Classe principale de l'application.
 */
public class Application {

    /**
     * Point d'entrée du programme.
     *
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Utiliser le look and feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration du look and feel: " + e.getMessage());
        }

        // Créer et afficher l'écran de chargement
        SplashScreen splash = new SplashScreen();

        splash.showSplashWhile(() -> {
            try {
                // Initialisation lourde ici
                TextureManager.getInstance().loadTextures();

                // Créer et afficher l'interface utilisateur
                SwingUtilities.invokeLater(() -> {
                    UserInterface ui = new UserInterface();
                    ui.display();
                });
            } catch (Exception e) {
                e.printStackTrace();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Erreur lors du démarrage de l'application:\n" + e.getMessage(),
                            "Erreur fatale",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                });
            }
        });
    }
}
