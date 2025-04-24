import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Interface utilisateur principale de l'application avec design personnalisé.
 */
public class UserInterface extends JFrame {
    private final JTabbedPane tabbedPane;
    private final GradientPanel gradientPanel;
    private final ColorToBlockPanel colorToBlockPanel;
    private final PixelArtPanel pixelArtPanel;
    private final PerlinNoisePanel perlinNoisePanel;
    private Point initialClick;
    private Rectangle normalBounds;
    private final Rectangle maxBounds;
    private boolean isMaximized = false;

    // Couleurs et polices
    private static final Color bgColor = new Color(32, 34, 37);
    private static final Color blockColor = new Color(47, 49, 54);
    private static final Color accentColor = new Color(114, 137, 218);
    private static final Color tabBorderColor = new Color(114, 137, 218);
    private static final Color textColor = Color.WHITE;
    private static final Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font titleFont = new Font("Segoe UI", Font.BOLD, 14);

    // UI constants
    private static final int WINDOW_CORNER_RADIUS = 12;
    private static final int TITLE_BAR_HEIGHT = 36;

    public UserInterface() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Zone utilisable (exclut la barre des tâches)
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        maxBounds = ge.getMaximumWindowBounds();

        // Panneaux
        tabbedPane = new JTabbedPane();
        gradientPanel = new GradientPanel();
        colorToBlockPanel = new ColorToBlockPanel();
        pixelArtPanel = new PixelArtPanel();
        perlinNoisePanel = new PerlinNoisePanel();

        // Onglets
        tabbedPane.addTab("Linear Gradient Generator", new ImageIcon("icons/gradient.png"), gradientPanel);
        tabbedPane.addTab("Color to Bloc converter", new ImageIcon("icons/color.png"), colorToBlockPanel);
        tabbedPane.addTab("Image to Pixel-Art Converter", new ImageIcon("icons/pixelart.png"), pixelArtPanel);
        tabbedPane.addTab("Perlin Noise Generator", new ImageIcon("icons/perlin.png"), perlinNoisePanel);

        applyDarkTheme();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);

        root.add(createTitleBar(), BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bgColor);
        content.add(tabbedPane, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);

        normalBounds = new Rectangle(100, 100, 1000, 700);
        setBounds(normalBounds);
        setMinimumSize(new Dimension(800, 600));
        applyWindowShape();
        setLocationRelativeTo(null);
    }

    private void applyDarkTheme() {
        // Coloration des onglets et contenu
        tabbedPane.setBackground(blockColor);
        tabbedPane.setForeground(textColor);
        tabbedPane.setFont(uiFont);
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(blockColor);
                g2.fillRect(x, y, w, h);
                g2.dispose();
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Supprimer la bordure du contenu
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tabBorderColor);
                g2.drawRoundRect(x, y, w - 1, h - 1, 10, 10);
                g2.dispose();
            }
        });

        // Autres composants
        UIManager.put("Label.foreground", textColor);
        UIManager.put("Button.background", blockColor);
        UIManager.put("Button.foreground", textColor);
        UIManager.put("Button.font", uiFont);
        UIManager.put("Panel.background", bgColor);
        UIManager.put("ScrollPane.background", bgColor);
    }

    private JPanel createTitleBar() {
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(blockColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(maxBounds.width, TITLE_BAR_HEIGHT));
        bar.setLayout(new BorderLayout());

        JLabel icon = new JLabel(new ImageIcon("icons/app_icon.png"));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        JLabel title = new JLabel("SkymcDB v1.3");
        title.setForeground(textColor);
        title.setFont(titleFont);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(icon);
        left.add(title);
        bar.add(left, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controls.setOpaque(false);
        JButton min = makeButton("—");
        min.addActionListener(e -> setState(JFrame.ICONIFIED));
        JButton max = makeButton("□");
        max.addActionListener(e -> toggleMaximize());
        JButton close = makeButton("×");
        close.setForeground(new Color(232, 17, 35));
        close.addActionListener(e -> System.exit(0));
        controls.add(min);
        controls.add(max);
        controls.add(close);
        bar.add(controls, BorderLayout.EAST);

        bar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!isMaximized) initialClick = e.getPoint();
            }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!isMaximized) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - initialClick.x, loc.y + e.getY() - initialClick.y);
                }
            }
        });

        return bar;
    }

    private void toggleMaximize() {
        if (!isMaximized) {
            normalBounds = getBounds();
            setBounds(maxBounds);
            isMaximized = true;
        } else {
            setBounds(normalBounds);
            isMaximized = false;
        }
        applyWindowShape();
    }

    private void applyWindowShape() {
        if (!isMaximized) {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), WINDOW_CORNER_RADIUS, WINDOW_CORNER_RADIUS));
        } else {
            setShape(null);
        }
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setForeground(textColor);
        btn.setPreferredSize(new Dimension(30, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setOpaque(true); btn.setBackground(accentColor.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setOpaque(false); }
        });
        return btn;
    }

    public void display() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
