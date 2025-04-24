import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ColorToBlockPanel extends JPanel {
    private final ColorToBlockConverter converter;
    private final JPanel resultsPanel;
    private final JScrollPane resultsScrollPane;
    private final JRadioButton topTextureRadio;
    private final JRadioButton sideTextureRadio;
    private final JSpinner resultsCountSpinner;
    private final JButton findButton;

    private final HSVColorCircle hsvCircle;
    private final JPanel colorPreview;
    private final JSlider hueSlider;
    private final JSlider satSlider;
    private final JSlider valSlider;
    private final JTextField hexField;

    private float currentHue = 0f;
    private float currentSat = 1f;
    private float currentVal = 1f;

    public ColorToBlockPanel() {
        this.converter = new ColorToBlockConverter();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(54, 57, 63));

        // === PANEL COULEUR ===
        JPanel colorPanel = new JPanel(new BorderLayout(10, 10));
        colorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Color Selection", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        colorPanel.setBackground(new Color(47, 49, 54));

        // Création du cercle HSV
        hsvCircle = new HSVColorCircle();
        hsvCircle.addColorChangeListener(this::updateColorFromCircle);
        
        // Panneau pour centrer le cercle HSV
        JPanel circlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        circlePanel.setBackground(new Color(47, 49, 54));
        circlePanel.add(hsvCircle);
        
        // Rectangle de prévisualisation
        colorPreview = new JPanel();
        int previewHeight = 40;
        colorPreview.setPreferredSize(new Dimension(hsvCircle.getRadius() * 2, previewHeight));
        colorPreview.setMaximumSize(new Dimension(hsvCircle.getRadius() * 2, previewHeight));
        colorPreview.setBackground(Color.getHSBColor(currentHue, currentSat, currentVal));
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        // Centrer l'aperçu de couleur
        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewPanel.setBackground(new Color(47, 49, 54));
        previewPanel.add(colorPreview);
        
        // Champ pour le code hexadécimal
        JPanel hexPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hexPanel.setBackground(new Color(47, 49, 54));
        
        JLabel hexLabel = new JLabel("Hex Code : #");
        hexLabel.setForeground(Color.WHITE);
        hexField = new JTextField(6);
        hexField.setText(colorToHex(Color.getHSBColor(currentHue, currentSat, currentVal)));
        
        hexField.addActionListener(e -> updateColorFromHex());
        hexField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateColorFromHex();
            }
        });
        
        hexPanel.add(hexLabel);
        hexPanel.add(hexField);
        
        // Sliders
        JPanel slidersPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        slidersPanel.setBackground(new Color(47, 49, 54));
        
        hueSlider = createSlider("HUE Tint", 0, 100, 0);
        satSlider = createSlider("Saturation", 0, 100, 100);
        valSlider = createSlider("Brightness", 0, 100, 100);

        hueSlider.addChangeListener(e -> updateColorFromSliders());
        satSlider.addChangeListener(e -> updateColorFromSliders());
        valSlider.addChangeListener(e -> updateColorFromSliders());
        
        slidersPanel.add(hueSlider);
        slidersPanel.add(satSlider);
        slidersPanel.add(valSlider);
        
        // Création d'un conteneur unique avec bordure pour tous les éléments de sélection de couleur
        JPanel colorSelectionContainer = new JPanel();
        colorSelectionContainer.setLayout(new BoxLayout(colorSelectionContainer, BoxLayout.Y_AXIS));
        colorSelectionContainer.setBackground(new Color(47, 49, 54));
        colorSelectionContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // Ajout de tous les éléments dans le conteneur unique avec espacement réduit
        colorSelectionContainer.add(circlePanel);
        colorSelectionContainer.add(Box.createRigidArea(new Dimension(0, 5))); // Réduit de 10 à 5
        colorSelectionContainer.add(previewPanel);
        colorSelectionContainer.add(Box.createRigidArea(new Dimension(0, 3))); // Réduit de 5 à 3
        colorSelectionContainer.add(hexPanel);
        colorSelectionContainer.add(Box.createRigidArea(new Dimension(0, 5))); // Réduit de 10 à 5
        colorSelectionContainer.add(slidersPanel);
        
        // Ajout du conteneur unique au panneau de couleur principal
        colorPanel.add(colorSelectionContainer, BorderLayout.CENTER);

        // === PANEL OPTIONS ===
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Options", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        optionsPanel.setBackground(new Color(47, 49, 54));

        topTextureRadio = new JRadioButton("Use the Top texture", ConfigManager.getInstance().getBooleanProperty("useTopTexture", true));
        topTextureRadio.setForeground(Color.WHITE);
        topTextureRadio.setBackground(new Color(47, 49, 54));
        sideTextureRadio = new JRadioButton("Use the Side texture", !ConfigManager.getInstance().getBooleanProperty("useTopTexture", true));
        sideTextureRadio.setForeground(Color.WHITE);
        sideTextureRadio.setBackground(new Color(47, 49, 54));

        ButtonGroup textureGroup = new ButtonGroup();
        textureGroup.add(topTextureRadio);
        textureGroup.add(sideTextureRadio);

        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    countPanel.setBackground(new Color(47, 49, 54));

    // Créer le JLabel avec la couleur blanche explicite
    JLabel countLabel = new JLabel("Results amount:");
    countLabel.setForeground(Color.WHITE);  // Définir la couleur du texte en blanc

    countPanel.add(countLabel);
    countPanel.setForeground(Color.WHITE);  // Ceci n'affecte pas les composants enfants

    // Personnaliser également le JSpinner pour qu'il corresponde au thème
    resultsCountSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
    JComponent editor = resultsCountSpinner.getEditor();
    if (editor instanceof JSpinner.DefaultEditor) {
        JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(32, 34, 37));  // Couleur plus sombre pour le champ de texte
    }

    countPanel.add(resultsCountSpinner);

        findButton = new JButton("Search for Blocs");
        findButton.setBackground(new Color(114, 137, 218));
        findButton.setForeground(Color.WHITE);
        findButton.setFocusPainted(false);
        findButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        optionsPanel.add(topTextureRadio);
        optionsPanel.add(sideTextureRadio);
        optionsPanel.add(countPanel);
        optionsPanel.add(findButton);

        // === PANEL GAUCHE ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(47, 49, 54));
        leftPanel.add(colorPanel, BorderLayout.CENTER);
        leftPanel.add(optionsPanel, BorderLayout.SOUTH);

        // === PANEL RÉSULTATS ===
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(47, 49, 54));

        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        add(leftPanel, BorderLayout.WEST);
        add(resultsScrollPane, BorderLayout.CENTER);

        // === EVENTS ===
        findButton.addActionListener(e -> findMatchingBlocks());

        topTextureRadio.addActionListener(e -> ConfigManager.getInstance().setBooleanProperty("useTopTexture", true));
        sideTextureRadio.addActionListener(e -> ConfigManager.getInstance().setBooleanProperty("useTopTexture", false));
    }

    private JSlider createSlider(String title, int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setBackground(new Color(47, 49, 54));
        slider.setForeground(Color.WHITE);
        slider.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));
        return slider;
    }

    private void updateColorFromCircle(Color c) {
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        currentHue = hsv[0];
        currentSat = hsv[1];
        // Gardons la valeur de luminosité actuelle
        hueSlider.setValue((int) (currentHue * 100));
        satSlider.setValue((int) (currentSat * 100));
        updatePreview();
    }

    private void updateColorFromSliders() {
        currentHue = hueSlider.getValue() / 100f;
        currentSat = satSlider.getValue() / 100f;
        currentVal = valSlider.getValue() / 100f;
        Color c = Color.getHSBColor(currentHue, currentSat, currentVal);
        hsvCircle.setColor(c);
        hsvCircle.setColorValue(currentVal); // Mettre à jour la luminosité du cercle
        updatePreview();
    }
    
    private void updateColorFromHex() {
        try {
            String hex = hexField.getText().replaceAll("#", "").trim();
            Color c = Color.decode("#" + hex);
            float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            currentHue = hsv[0];
            currentSat = hsv[1];
            currentVal = hsv[2];
            
            // Mettre à jour les sliders sans déclencher leurs événements
            hueSlider.removeChangeListener(hueSlider.getChangeListeners()[0]);
            satSlider.removeChangeListener(satSlider.getChangeListeners()[0]);
            valSlider.removeChangeListener(valSlider.getChangeListeners()[0]);
            
            hueSlider.setValue((int) (currentHue * 100));
            satSlider.setValue((int) (currentSat * 100));
            valSlider.setValue((int) (currentVal * 100));
            
            hueSlider.addChangeListener(e -> updateColorFromSliders());
            satSlider.addChangeListener(e -> updateColorFromSliders());
            valSlider.addChangeListener(e -> updateColorFromSliders());
            
            hsvCircle.setColor(c);
            hsvCircle.setColorValue(currentVal);
            updatePreview();
        } catch (NumberFormatException e) {
            // Ignorer si le format est invalide et remettre le code hex correct
            hexField.setText(colorToHex(Color.getHSBColor(currentHue, currentSat, currentVal)));
        }
    }

    private void updatePreview() {
        Color currentColor = Color.getHSBColor(currentHue, currentSat, currentVal);
        colorPreview.setBackground(currentColor);
        hexField.setText(colorToHex(currentColor));
    }
    
    private String colorToHex(Color color) {
        return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void findMatchingBlocks() {
        Color selectedColor = Color.getHSBColor(currentHue, currentSat, currentVal);
        boolean useTopTexture = topTextureRadio.isSelected();
        int maxResults = (Integer) resultsCountSpinner.getValue();

        List<Block> matchingBlocks = converter.findClosestBlocks(selectedColor, useTopTexture, maxResults);
        resultsPanel.removeAll();

        for (Block block : matchingBlocks) {
            resultsPanel.add(createBlockResultPanel(block, selectedColor, useTopTexture));
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createBlockResultPanel(Block block, Color targetColor, boolean useTopTexture) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(54, 57, 63));

        JLabel textureLabel = new JLabel();
        BufferedImage texture = useTopTexture ? block.getTopTexture() : block.getSideTexture();
        if (texture != null) {
            ImageIcon icon = new ImageIcon(texture.getScaledInstance(48, 48, Image.SCALE_SMOOTH));
            textureLabel.setIcon(icon);
        }

        JLabel nameLabel = new JLabel(block.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);

        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        colorPanel.setBackground(new Color(47, 49, 54));

        Color blockColor = useTopTexture ? block.getAverageTopColor() : block.getAverageSideColor();
        JPanel blockColorPanel = new JPanel();
        blockColorPanel.setBackground(blockColor);
        blockColorPanel.setPreferredSize(new Dimension(30, 15));
        blockColorPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        double distance = ColorUtils.colorDistance(targetColor, blockColor);
        JLabel distanceLabel = new JLabel(String.format("Distance: %.2f", distance));
        distanceLabel.setForeground(Color.WHITE);

        colorPanel.add(blockColorPanel);
        colorPanel.add(distanceLabel);

        panel.add(textureLabel, BorderLayout.WEST);
        panel.add(nameLabel, BorderLayout.CENTER);
        panel.add(colorPanel, BorderLayout.EAST);

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        panel.add(separator, BorderLayout.SOUTH);

        return panel;
    }

    // === CLASSE INTERNE : HSVColorCircle ===
    private static class HSVColorCircle extends JComponent {
        private BufferedImage circleImage;
        private final int radius = 115;
        private float hue = 0f;
        private float saturation = 1f;
        private float value = 1f; // Ajout de la valeur de luminosité
        private ColorChangeListener listener;

        public HSVColorCircle() {
            setPreferredSize(new Dimension(radius * 2, radius * 2));
            generateImage();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouse(e.getX(), e.getY());
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouse(e.getX(), e.getY());
                }
            });
        }
        
        public int getRadius() {
            return radius;
        }

        private void generateImage() {
            circleImage = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
            updateCircleImage();
        }
        
        private void updateCircleImage() {
            for (int x = 0; x < radius * 2; x++) {
                for (int y = 0; y < radius * 2; y++) {
                    int dx = x - radius;
                    int dy = y - radius;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist <= radius) {
                        float sat = (float) dist / radius;
                        float angle = (float) (Math.atan2(dy, dx) / (2 * Math.PI));
                        if (angle < 0) angle += 1;
                        int rgb = Color.HSBtoRGB(angle, sat, value); // Utiliser la valeur de luminosité actuelle
                        circleImage.setRGB(x, y, rgb);
                    } else {
                        circleImage.setRGB(x, y, 0x00000000);
                    }
                }
            }
        }

        private void handleMouse(int x, int y) {
            int dx = x - radius;
            int dy = y - radius;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= radius) {
                saturation = (float) Math.min(1.0, dist / radius);
                hue = (float) (Math.atan2(dy, dx) / (2 * Math.PI));
                if (hue < 0) hue += 1.0f;
                repaint();
                if (listener != null) {
                    listener.colorChanged(Color.getHSBColor(hue, saturation, value));
                }
            }
        }

        public void setColor(Color color) {
            float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            hue = hsv[0];
            saturation = hsv[1];
            // Ne pas modifier la valeur 'value' ici pour conserver la luminosité
            repaint();
        }
        
        public void setColorValue(float value) {
            this.value = value;
            updateCircleImage(); // Mettre à jour l'image du cercle avec la nouvelle luminosité
            repaint();
        }

        public void addColorChangeListener(ColorChangeListener listener) {
            this.listener = listener;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(circleImage, 0, 0, null);
            
            // Conversion en Graphics2D pour plus de contrôle
            Graphics2D g2d = (Graphics2D) g;
            
            // Sauvegarder l'état actuel
            Stroke oldStroke = g2d.getStroke();
            
            // Dessiner le cercle extérieur avec une épaisseur de 2 pixels
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(3.0f)); // Définir l'épaisseur à 2 pixels
            g2d.drawOval(0, 0, radius * 2, radius * 2);
            
            // Restaurer l'état précédent pour les autres dessins
            g2d.setStroke(oldStroke);

            // Dessiner le sélecteur
            int selX = (int) (radius + Math.cos(hue * 2 * Math.PI) * saturation * radius);
            int selY = (int) (radius + Math.sin(hue * 2 * Math.PI) * saturation * radius);

            g.setColor(Color.WHITE);
            g.drawOval(selX - 5, selY - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawOval(selX - 4, selY - 4, 8, 8);
        }

        public interface ColorChangeListener {
            void colorChanged(Color newColor);
        }
    }
}