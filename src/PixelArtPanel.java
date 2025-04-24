import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.io.File;

/**
 * Panel représentant l'onglet du convertisseur de pixel art avec style Discord.
 */
public class PixelArtPanel extends JPanel {
    // Constantes de couleurs pour le thème Discord
    private static final Color DISCORD_DARK = new Color(54, 57, 63);
    private static final Color DISCORD_DARKER = new Color(47, 49, 54);
    private static final Color DISCORD_DARKEST = new Color(32, 34, 37);
    private static final Color DISCORD_LIGHT_TEXT = new Color(220, 221, 222);
    private static final Color DISCORD_ACCENT = new Color(114, 137, 218); // Bleu Discord
    private static final Color DISCORD_RED = new Color(240, 71, 71);
    private static final Color DISCORD_GREEN = new Color(67, 181, 129);
    
    private final JButton loadButton;
    private final JButton convertButton;
    private final JButton saveButton;
    private final JRadioButton topTextureRadio;
    private final JRadioButton sideTextureRadio;
    private final JSpinner heightSpinner;
    private final JLabel imagePreviewLabel;
    private final JLabel resultPreviewLabel;
    private final JScrollPane resultScrollPane;
    private final PixelArtConverter converter;
    private final ConfigManager configManager;
    
    private BufferedImage sourceImage;
    private BufferedImage resultImage;
    private File lastDirectory;
    
    // Variables pour le zoom
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private double zoomFactor = 1.0;
    private final double ZOOM_STEP = 0.25;
    private final double MIN_ZOOM = 0.25;
    private final double MAX_ZOOM = 10.0;
    private JPanel resultPanel;
    
    /**
     * Constructeur.
     */
    public PixelArtPanel() {
        this.converter = new PixelArtConverter();
        this.configManager = ConfigManager.getInstance();
        
        // Appliquer le thème sombre global
        setupDiscordTheme();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(DISCORD_DARKER);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel des contrôles
        JPanel controlsPanel = createStyledPanel();
        controlsPanel.setLayout(new GridLayout(0, 1, 8, 8));
        controlsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DISCORD_DARKEST, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        // Titre du panneau des contrôles
        JLabel controlsTitle = new JLabel("CONTROLS");
        controlsTitle.setForeground(DISCORD_LIGHT_TEXT);
        controlsTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        controlsPanel.add(controlsTitle);
        
        // Boutons avec style Discord
        loadButton = createStyledButton("Load an image", DISCORD_ACCENT);
        convertButton = createStyledButton("Convert to pixel art", DISCORD_ACCENT);
        saveButton = createStyledButton("Save the result", DISCORD_GREEN);
        saveButton.setEnabled(false);
        
        // Groupe de boutons radio pour le type de texture
        ButtonGroup textureGroup = new ButtonGroup();
        topTextureRadio = createStyledRadioButton("Use the top texture", 
                configManager.getBooleanProperty("useTopTexture", true));
        sideTextureRadio = createStyledRadioButton("Use the side texture", 
                !configManager.getBooleanProperty("useTopTexture", true));
        textureGroup.add(topTextureRadio);
        textureGroup.add(sideTextureRadio);
        
        // Hauteur du pixel art
        JPanel heightPanel = createStyledPanel();
        heightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel heightLabel = new JLabel("Height (in blocs):");
        heightLabel.setForeground(DISCORD_LIGHT_TEXT);
        heightPanel.add(heightLabel);
        
        heightSpinner = new JSpinner(new SpinnerNumberModel(
                configManager.getIntProperty("pixelArtHeight", 32),
                8, 256, 8));
        styleSpinner(heightSpinner);
        heightPanel.add(heightSpinner);
        
        // Séparateur
        controlsPanel.add(Box.createVerticalStrut(5));
        
        // Ajouter les contrôles au panel
        controlsPanel.add(loadButton);
        controlsPanel.add(Box.createVerticalStrut(10));
        
        JLabel optionsTitle = new JLabel("OPTIONS");
        optionsTitle.setForeground(DISCORD_LIGHT_TEXT);
        optionsTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        controlsPanel.add(optionsTitle);
        
        controlsPanel.add(topTextureRadio);
        controlsPanel.add(sideTextureRadio);
        controlsPanel.add(heightPanel);
        controlsPanel.add(Box.createVerticalStrut(15));
        controlsPanel.add(convertButton);
        controlsPanel.add(saveButton);
        
        // Panel de prévisualisation
        JPanel previewsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        previewsPanel.setOpaque(false);
        
        // Prévisualisation de l'image source
        JPanel sourcePanel = createStyledPanel();
        sourcePanel.setLayout(new BorderLayout());
        sourcePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DISCORD_DARKEST, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        JLabel sourceTitleLabel = new JLabel("IMAGE SOURCE");
        sourceTitleLabel.setForeground(DISCORD_LIGHT_TEXT);
        sourceTitleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        sourcePanel.add(sourceTitleLabel, BorderLayout.NORTH);
        
        imagePreviewLabel = createPreviewLabel("No image loaded");
        JScrollPane sourceScrollPane = createStyledScrollPane(imagePreviewLabel);
        sourcePanel.add(sourceScrollPane, BorderLayout.CENTER);
        
        // Prévisualisation du résultat
        resultPanel = createStyledPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DISCORD_DARKEST, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        JLabel resultTitleLabel = new JLabel("PIXEL ART");
        resultTitleLabel.setForeground(DISCORD_LIGHT_TEXT);
        resultTitleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        resultPanel.add(resultTitleLabel, BorderLayout.NORTH);
        
        resultPreviewLabel = createPreviewLabel("No result");
        resultScrollPane = createStyledScrollPane(resultPreviewLabel);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        // Ajouter les contrôles de zoom
        addZoomControls();
        
        previewsPanel.add(sourcePanel);
        previewsPanel.add(resultPanel);
        
        // Ajouter à la fenêtre principale
        add(controlsPanel, BorderLayout.WEST);
        add(previewsPanel, BorderLayout.CENTER);
        
        // Initialiser le dernier répertoire
        lastDirectory = new File(configManager.getProperty("lastDirectory", System.getProperty("user.home")));
        
        // Ajouter les listeners
        setupListeners();
    }
    
    /**
     * Ajoute les contrôles de zoom au panel de résultat.
     */
    private void addZoomControls() {
        // Créer un panel pour les boutons de zoom
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        zoomPanel.setBackground(DISCORD_DARK);
        
        // Créer les boutons de zoom
        zoomInButton = createStyledButton("+", DISCORD_ACCENT);
        zoomOutButton = createStyledButton("-", DISCORD_ACCENT);
        
        // Personnaliser les boutons pour qu'ils soient plus petits
        Dimension buttonSize = new Dimension(30, 30);
        zoomInButton.setPreferredSize(buttonSize);
        zoomOutButton.setPreferredSize(buttonSize);
        
        // Créer un label pour afficher le niveau de zoom actuel
        JLabel zoomLabel = new JLabel("100%");
        zoomLabel.setForeground(DISCORD_LIGHT_TEXT);
        zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        zoomLabel.setPreferredSize(new Dimension(60, 30));
        
        // Ajouter les listeners
        zoomInButton.addActionListener(e -> {
            if (zoomFactor < MAX_ZOOM) {
                zoomFactor += ZOOM_STEP;
                updateZoomLabel(zoomLabel);
                updateResultPreview();
            }
        });
        
        zoomOutButton.addActionListener(e -> {
            if (zoomFactor > MIN_ZOOM) {
                zoomFactor -= ZOOM_STEP;
                updateZoomLabel(zoomLabel);
                updateResultPreview();
            }
        });
        
        // Ajouter les boutons au panel
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(zoomLabel);
        zoomPanel.add(zoomInButton);
        
        // Ajouter le panel de zoom au bas du panel de résultat
        resultPanel.add(zoomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Met à jour le label affichant le pourcentage de zoom.
     */
    private void updateZoomLabel(JLabel zoomLabel) {
        int percentage = (int) (zoomFactor * 100);
        zoomLabel.setText(percentage + "%");
    }
    
    /**
     * Configure le thème Discord pour tous les composants Swing.
     */
    private void setupDiscordTheme() {
        UIManager.put("Panel.background", DISCORD_DARKER);
        UIManager.put("OptionPane.background", DISCORD_DARKER);
        UIManager.put("OptionPane.messageForeground", DISCORD_LIGHT_TEXT);
        UIManager.put("OptionPane.messageBackground", DISCORD_DARKER);
        UIManager.put("OptionPane.buttonBackground", DISCORD_DARK);
        UIManager.put("OptionPane.buttonForeground", DISCORD_LIGHT_TEXT);
        UIManager.put("Button.background", DISCORD_ACCENT);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", DISCORD_ACCENT.darker());
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("RadioButton.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("Label.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("ComboBox.background", DISCORD_DARK);
        UIManager.put("ComboBox.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("ScrollPane.background", DISCORD_DARK);
        UIManager.put("ScrollBar.thumb", DISCORD_DARK);
        UIManager.put("ScrollBar.track", DISCORD_DARKER);
        UIManager.put("TextField.background", DISCORD_DARKEST);
        UIManager.put("TextField.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("TextField.caretForeground", DISCORD_LIGHT_TEXT);
        UIManager.put("TextArea.background", DISCORD_DARKEST);
        UIManager.put("TextArea.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("FileChooser.background", DISCORD_DARKER);
        UIManager.put("FileChooser.foreground", DISCORD_LIGHT_TEXT);
    }
    
    /**
     * Crée un bouton stylisé comme Discord.
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un panel stylisé comme Discord.
     */
    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DISCORD_DARK);
        return panel;
    }
    
    /**
     * Crée un bouton radio stylisé comme Discord.
     */
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text, selected);
        radioButton.setForeground(DISCORD_LIGHT_TEXT);
        radioButton.setBackground(DISCORD_DARK);
        radioButton.setFocusPainted(false);
        radioButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        return radioButton;
    }
    
    /**
     * Crée un label pour prévisualisation stylisé comme Discord.
     */
    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(DISCORD_LIGHT_TEXT);
        label.setBackground(DISCORD_DARKEST);
        label.setOpaque(true);
        return label;
    }
    
    /**
     * Crée un scrollPane stylisé comme Discord.
     */
    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBackground(DISCORD_DARKEST);
        scrollPane.getViewport().setBackground(DISCORD_DARKEST);
        scrollPane.setBorder(BorderFactory.createLineBorder(DISCORD_DARKEST));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = DISCORD_DARK;
                this.trackColor = DISCORD_DARKEST;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        return scrollPane;
    }
    
    /**
     * Style le spinner pour le thème Discord.
     */
    private void styleSpinner(JSpinner spinner) {
        spinner.setBackground(DISCORD_DARKEST);
        spinner.setForeground(DISCORD_LIGHT_TEXT);
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(DISCORD_DARKEST);
            textField.setForeground(DISCORD_LIGHT_TEXT);
            textField.setCaretColor(DISCORD_LIGHT_TEXT);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DISCORD_DARKEST),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
            ));
        }
        
        // Personnaliser les boutons du spinner
        for (Component comp : spinner.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(DISCORD_DARK);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            }
        }
    }
    
    /**
     * Configure les listeners des composants.
     */
    private void setupListeners() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertImage();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveResult();
            }
        });
        
        topTextureRadio.addActionListener(e -> {
            configManager.setBooleanProperty("useTopTexture", true);
        });
        
        sideTextureRadio.addActionListener(e -> {
            configManager.setBooleanProperty("useTopTexture", false);
        });
        
        heightSpinner.addChangeListener(e -> {
            configManager.setIntProperty("pixelArtHeight", (Integer) heightSpinner.getValue());
        });
    }
    
    /**
     * Charge une image à partir d'un fichier.
     */
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser(lastDirectory);
        fileChooser.setDialogTitle("Load an image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        
        // Appliquer le thème sombre au JFileChooser (limité par les capacités de Swing)
        setFileChooserStyle(fileChooser);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            lastDirectory = selectedFile.getParentFile();
            configManager.setProperty("lastDirectory", lastDirectory.getAbsolutePath());
            
            try {
                sourceImage = converter.loadImage(selectedFile.getAbsolutePath());
                
                if (sourceImage != null) {
                    // Mettre à jour la prévisualisation
                    updateSourcePreview();
                    convertButton.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement de l'image: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Tente d'appliquer le style Discord au JFileChooser.
     */
    private void setFileChooserStyle(JFileChooser fileChooser) {
        // Définir quelques propriétés UI supplémentaires pour JFileChooser
        UIManager.put("FileChooser.background", DISCORD_DARKER);
        UIManager.put("FileChooser.foreground", DISCORD_LIGHT_TEXT);
        UIManager.put("FileChooser.listViewBackground", DISCORD_DARK);
        UIManager.put("FileChooser.listViewForeground", DISCORD_LIGHT_TEXT);
        
        fileChooser.setBackground(DISCORD_DARKER);
        fileChooser.setForeground(DISCORD_LIGHT_TEXT);
        
        // Personnaliser les composants
        for (Component c : fileChooser.getComponents()) {
            styleComponent(c);
        }
    }
    
    private void styleComponent(Component c) {
        c.setBackground(DISCORD_DARKER);
        
        // Traitement spécifique selon le type de composant
        if (c instanceof JLabel) {
            ((JLabel) c).setForeground(DISCORD_LIGHT_TEXT);
        }
        else if (c instanceof JPanel) {
            ((JPanel) c).setBackground(DISCORD_DARKER);
        }
        else if (c instanceof JButton) {
            JButton button = (JButton) c;
            button.setBackground(DISCORD_ACCENT);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
        }
        else if (c instanceof JTextField) {
            JTextField textField = (JTextField) c;
            textField.setBackground(DISCORD_DARKEST);
            textField.setForeground(DISCORD_LIGHT_TEXT);
            textField.setCaretColor(DISCORD_LIGHT_TEXT);
        }
        // Traitement spécial pour la liste de fichiers
        else if (c instanceof JList) {
            JList<?> list = (JList<?>) c;
            list.setBackground(DISCORD_DARK);
            list.setForeground(DISCORD_LIGHT_TEXT);
            list.setSelectionBackground(DISCORD_ACCENT);
            list.setSelectionForeground(Color.WHITE);
        }
        // Traitement spécial pour les tables (aussi utilisées dans JFileChooser)
        else if (c instanceof JTable) {
            JTable table = (JTable) c;
            table.setBackground(DISCORD_DARK);
            table.setForeground(DISCORD_LIGHT_TEXT);
            table.setSelectionBackground(DISCORD_ACCENT);
            table.setSelectionForeground(Color.WHITE);
            table.setGridColor(DISCORD_DARKEST);
        }
        else if (c instanceof JScrollPane) {
            ((JScrollPane) c).getViewport().setBackground(DISCORD_DARK);
        }
        
        // Appel récursif pour les conteneurs
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                styleComponent(child);
            }
        }
    }
    
    /**
     * Convertit l'image en pixel art.
     */
    private void convertImage() {
        if (sourceImage == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez d'abord charger une image.",
                    "Aucune image",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            int targetHeight = (Integer) heightSpinner.getValue();
            boolean useTopTexture = topTextureRadio.isSelected();
            
            resultImage = converter.convertToPixelArt(sourceImage, targetHeight, useTopTexture);
            
            // Réinitialiser le zoom quand une nouvelle image est convertie
            zoomFactor = 1.0;
            
            // Mettre à jour la prévisualisation
            updateResultPreview();
            saveButton.setEnabled(true);
            
            // Mettre à jour le label de zoom s'il existe
            for (Component c : resultPanel.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel panel = (JPanel) c;
                    for (Component innerComp : panel.getComponents()) {
                        if (innerComp instanceof JLabel && !(innerComp instanceof JButton)) {
                            JLabel label = (JLabel) innerComp;
                            if (label.getPreferredSize().width == 60) {  // C'est notre label de zoom
                                updateZoomLabel(label);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la conversion: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    /**
     * Enregistre le résultat dans un fichier.
     */
    private void saveResult() {
        if (resultImage == null) {
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser(lastDirectory);
        fileChooser.setDialogTitle("Enregistrer le pixel art");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images PNG", "png"));
        
        // Appliquer le thème sombre au JFileChooser
        setFileChooserStyle(fileChooser);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            lastDirectory = selectedFile.getParentFile();
            configManager.setProperty("lastDirectory", lastDirectory.getAbsolutePath());
            
            // Ajouter l'extension .png si nécessaire
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                filePath += ".png";
            }
            
            try {
                boolean success = converter.savePixelArt(resultImage, filePath);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Pixel art enregistré avec succès.",
                            "Enregistrement réussi",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'enregistrement du pixel art.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Met à jour la prévisualisation de l'image source.
     */
    private void updateSourcePreview() {
        if (sourceImage != null) {
            int maxPreviewSize = 300;
            
            // Redimensionner pour l'affichage
            double ratio = Math.min(
                    (double) maxPreviewSize / sourceImage.getWidth(),
                    (double) maxPreviewSize / sourceImage.getHeight());
            
            int previewWidth = (int) (sourceImage.getWidth() * ratio);
            int previewHeight = (int) (sourceImage.getHeight() * ratio);
            
            BufferedImage previewImage = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = previewImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(sourceImage, 0, 0, previewWidth, previewHeight, null);
            g.dispose();
            
            imagePreviewLabel.setIcon(new ImageIcon(previewImage));
            imagePreviewLabel.setText(null);
        }
    }
    
    /**
     * Met à jour la prévisualisation du résultat.
     */
    private void updateResultPreview() {
        if (resultImage != null) {
            int maxPreviewSize = 500;
            
            // Calculer la taille de base d'affichage
            double baseRatio = 1.0;
            if (resultImage.getWidth() > maxPreviewSize || resultImage.getHeight() > maxPreviewSize) {
                baseRatio = Math.min(
                        (double) maxPreviewSize / resultImage.getWidth(),
                        (double) maxPreviewSize / resultImage.getHeight());
            }
            
            // Appliquer le facteur de zoom
            double ratio = baseRatio * zoomFactor;
            
            int previewWidth = (int) (resultImage.getWidth() * ratio);
            int previewHeight = (int) (resultImage.getHeight() * ratio);
            
            BufferedImage previewImage = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = previewImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(resultImage, 0, 0, previewWidth, previewHeight, null);
            g.dispose();
            
            resultPreviewLabel.setIcon(new ImageIcon(previewImage));
            resultPreviewLabel.setText(null);
        }
    }
}