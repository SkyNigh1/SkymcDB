import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.TitledBorder;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Panel représentant l'onglet du générateur de dégradé.
 */
public class GradientPanel extends JPanel {
    private final BlockSelector startBlockSelector;
    private final BlockSelector endBlockSelector;
    private final JSpinner stepsSpinner;
    private final JRadioButton topTextureRadio;
    private final JRadioButton sideTextureRadio;
    private final JPanel previewPanel;
    private final JButton generateButton;
    private final GradientGenerator gradientGenerator;
    private final ConfigManager configManager;
    
    // Couleurs Discord
    private static final Color DISCORD_DARK = new Color(54, 57, 63);
    private static final Color DISCORD_DARKER = new Color(47, 49, 54);
    private static final Color DISCORD_DARKEST = new Color(32, 34, 37);
    private static final Color DISCORD_LIGHT_TEXT = new Color(220, 221, 222);
    private static final Color DISCORD_ACCENT = new Color(114, 137, 218); // Bleu Discord
    private static final Color DISCORD_RED = new Color(240, 71, 71);
    private static final Color DISCORD_GREEN = new Color(67, 181, 129);
    
    // Police
    private static final Font uiFont = new Font("Segoe UI", Font.PLAIN, 13); // Police moderne
    
    /**
     * Constructeur.
     */
    public GradientPanel() {
        this.gradientGenerator = new GradientGenerator();
        this.configManager = ConfigManager.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(DISCORD_DARKEST);
        
        // Panel des paramètres
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(0, 1, 5, 5));
        settingsPanel.setBackground(DISCORD_DARKEST);

        // Groupe de boutons radio pour le type de texture
        ButtonGroup textureGroup = new ButtonGroup();
        topTextureRadio = new JRadioButton("Utiliser la texture du dessus", configManager.getBooleanProperty("useTopTexture", true));
        sideTextureRadio = new JRadioButton("Utiliser la texture latérale", !configManager.getBooleanProperty("useTopTexture", true));
        textureGroup.add(topTextureRadio);
        textureGroup.add(sideTextureRadio);
        
        // Personnalisation des boutons radio
        topTextureRadio.setForeground(DISCORD_LIGHT_TEXT);
        sideTextureRadio.setForeground(DISCORD_LIGHT_TEXT);
        topTextureRadio.setBackground(DISCORD_DARKER);
        sideTextureRadio.setBackground(DISCORD_DARKER);
        topTextureRadio.setFont(uiFont);
        sideTextureRadio.setFont(uiFont);
        
        // Panel pour le nombre d'étapes
        JPanel stepsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepsPanel.setBackground(DISCORD_DARKEST);
        JLabel stepsLabel = new JLabel("Nombre d'étapes:");
        stepsLabel.setForeground(DISCORD_LIGHT_TEXT);
        stepsLabel.setFont(uiFont);
        stepsPanel.add(stepsLabel);
        
        stepsSpinner = new JSpinner(new SpinnerNumberModel(
                configManager.getIntProperty("gradientSteps", 5),
                2, 32, 1));
        // Style pour le spinner
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) stepsSpinner.getEditor();
        editor.getTextField().setBackground(DISCORD_DARKER);
        editor.getTextField().setForeground(DISCORD_LIGHT_TEXT);
        editor.getTextField().setFont(uiFont);
        stepsPanel.add(stepsSpinner);
        
        // Bouton de génération
        generateButton = new JButton("Générer le dégradé");
        generateButton.setBackground(DISCORD_ACCENT);
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(uiFont);
        generateButton.setFocusPainted(false);
        generateButton.setBorderPainted(false);
        
        // Ajouter les composants au panel des paramètres
        settingsPanel.add(topTextureRadio);
        settingsPanel.add(sideTextureRadio);
        settingsPanel.add(stepsPanel);
        settingsPanel.add(generateButton);
        
        add(settingsPanel, BorderLayout.NORTH);
        
        // Panel des sélecteurs de blocs
        JPanel blocksPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        blocksPanel.setBackground(DISCORD_DARKEST);
        
        // Sélecteur du bloc de départ
        JPanel startPanel = new JPanel(new BorderLayout());
        startPanel.setBackground(DISCORD_DARK);
        JLabel startLabel = new JLabel("Bloc de départ:", JLabel.CENTER);
        startLabel.setFont(uiFont);
        startLabel.setForeground(DISCORD_LIGHT_TEXT);
        startPanel.add(startLabel, BorderLayout.NORTH);
        
        startBlockSelector = new BlockSelector(
                TextureManager.getInstance().getAllBlocks(),
                topTextureRadio.isSelected(),
                block -> {});
        startPanel.add(startBlockSelector, BorderLayout.CENTER);
        
        // Ajouter une bordure légère
        startPanel.setBorder(BorderFactory.createLineBorder(DISCORD_DARKER, 1));
        
        // Sélecteur du bloc d'arrivée
        JPanel endPanel = new JPanel(new BorderLayout());
        endPanel.setBackground(DISCORD_DARK);
        JLabel endLabel = new JLabel("Bloc d'arrivée:", JLabel.CENTER);
        endLabel.setFont(uiFont);
        endLabel.setForeground(DISCORD_LIGHT_TEXT);
        endPanel.add(endLabel, BorderLayout.NORTH);
        
        endBlockSelector = new BlockSelector(
                TextureManager.getInstance().getAllBlocks(),
                topTextureRadio.isSelected(),
                block -> {});
        endPanel.add(endBlockSelector, BorderLayout.CENTER);
        
        // Ajouter une bordure légère
        endPanel.setBorder(BorderFactory.createLineBorder(DISCORD_DARKER, 1));
        
        blocksPanel.add(startPanel);
        blocksPanel.add(endPanel);
        
        add(blocksPanel, BorderLayout.CENTER);
        
        // Panel de prévisualisation
        previewPanel = new JPanel(new BorderLayout());
        // Titre avec style Discord
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DISCORD_ACCENT, 1),
                "Aperçu du dégradé",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                DISCORD_LIGHT_TEXT);
        previewPanel.setBorder(titledBorder);
        previewPanel.setBackground(DISCORD_DARKER);
        
        JScrollPane previewScroll = new JScrollPane(previewPanel);
        previewScroll.getViewport().setBackground(DISCORD_DARKER);
        previewScroll.setBorder(BorderFactory.createLineBorder(DISCORD_DARK, 1));
        previewScroll.setPreferredSize(new Dimension(0, 150));
        
        add(previewScroll, BorderLayout.SOUTH);
        
        // Ajouter les listeners
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateGradient();
            }
        });
        
        topTextureRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configManager.setBooleanProperty("useTopTexture", true);
            }
        });
        
        sideTextureRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configManager.setBooleanProperty("useTopTexture", false);
            }
        });
        
        stepsSpinner.addChangeListener(e -> {
            configManager.setIntProperty("gradientSteps", (Integer) stepsSpinner.getValue());
        });
    }
    
    /**
     * Génère et affiche le dégradé.
     */
    private void generateGradient() {
        Block startBlock = startBlockSelector.getSelectedBlock();
        Block endBlock = endBlockSelector.getSelectedBlock();
        
        if (startBlock == null || endBlock == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un bloc de départ et un bloc d'arrivée.",
                    "Blocs non sélectionnés",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int steps = (Integer) stepsSpinner.getValue();
        boolean useTopTexture = topTextureRadio.isSelected();
        
        try {
            List<Block> gradientBlocks = gradientGenerator.generateBlockGradient(
                    startBlock, endBlock, steps, useTopTexture);
            
            BufferedImage gradientImage = gradientGenerator.createGradientImage(
                    gradientBlocks, 32, useTopTexture);
            
            // Afficher l'image dans le panel de prévisualisation
            previewPanel.removeAll();
            previewPanel.add(new JLabel(new ImageIcon(gradientImage)), BorderLayout.CENTER);
            
            // Ajouter les noms des blocs
            JPanel namesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            namesPanel.setBackground(DISCORD_DARKER);

            for (Block block : gradientBlocks) {
                JLabel nameLabel = new JLabel(block.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                nameLabel.setOpaque(true);
                nameLabel.setBackground(DISCORD_DARK);
                nameLabel.setForeground(DISCORD_LIGHT_TEXT);
                
                // Bord arrondi avec une bordure douce
                nameLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DISCORD_ACCENT, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
                namesPanel.add(nameLabel);
            }

            previewPanel.add(namesPanel, BorderLayout.SOUTH);
            
            previewPanel.revalidate();
            previewPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du dégradé: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}