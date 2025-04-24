import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        // Conteneur principal avec fond sombre et coins arrondis
        RoundedPanel content = new RoundedPanel(20, new Color(39, 40, 44));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Chargement du GIF
        ImageIcon gifIcon = new ImageIcon("assets/loading.gif");
        JLabel gifLabel = new JLabel(gifIcon);
        gifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texte "Loading..."
        JLabel textLabel = new JLabel("Loading...");
        textLabel.setForeground(new Color(200, 200, 200));
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Ajout au panel
        content.add(gifLabel);
        content.add(textLabel);

        setContentPane(content);
        pack();
        setLocationRelativeTo(null); // centré à l'écran
        setAlwaysOnTop(true);

        // Coins arrondis pour la fenêtre
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    // Affiche le splash pendant l'exécution d'une tâche lourde
    public void showSplashWhile(Runnable task) {
        setVisible(true);

        new Thread(() -> {
            task.run();
            SwingUtilities.invokeLater(() -> {
                setVisible(false);
                dispose();
            });
        }).start();
    }

    // JPanel personnalisé pour coins arrondis et fond
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color background;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.background = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
