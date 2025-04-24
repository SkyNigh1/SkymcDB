import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Panneau pour la génération de motifs Perlin Noise avec des blocs Minecraft.
 */
public class PerlinNoisePanel extends JPanel {
    // Gestionnaires et générateurs
    private final TextureManager textureManager;
    private final GradientGenerator gradientGenerator;
    private final ExecutorService executor;
    
    // Composants UI
    private BlockSelector startBlockSelector;
    private BlockSelector endBlockSelector;
    private JSlider intermediateBlocksSlider;
    private JSlider perlinScaleSlider;
    private JLabel intermediateBlocksLabel;
    private JLabel perlinScaleLabel;
    private JPanel previewPanel;
    private JButton generateButton;
    private JTextField commandOutputField;
    
    // Paramètres
    private boolean useTopTexture = true; // Toujours utiliser les textures du dessus
    private int intermediateBlockCount = 8;
    private double perlinScale = 8.0;
    private final int PREVIEW_SIZE = 50; // Dimensions de la preview en blocs (32x32)
    
    // Données
    private List<Block> gradientBlocks;
    private BufferedImage previewImage;
    
    /**
     * Constructeur.
     */
    public PerlinNoisePanel() {
        textureManager = TextureManager.getInstance();
        gradientGenerator = new GradientGenerator();
        executor = Executors.newSingleThreadExecutor();
        
        setupUI();
    }
    
    /**
     * Configuration de l'interface utilisateur.
     */
    private void setupUI() {
        // Configuration du panneau principal
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel des contrôles (à gauche)
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.WEST);
        
        // Panel de prévisualisation (à droite)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(new Color(54, 57, 63)); // Fond Discord
        
        previewPanel = createPreviewPanel();
        rightPanel.add(new JScrollPane(previewPanel), BorderLayout.CENTER);
        
        // Panel pour la commande FAWE
        JPanel commandPanel = new JPanel(new BorderLayout(5, 0));
        commandPanel.setBackground(new Color(54, 57, 63)); // Fond Discord
        commandPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel commandLabel = new JLabel("FAWE Command:");
        commandLabel.setForeground(Color.WHITE);
        commandPanel.add(commandLabel, BorderLayout.NORTH);
        
        commandOutputField = new JTextField();
        commandOutputField.setEditable(false);
        commandOutputField.setBackground(new Color(64, 68, 75)); // Fond de bloc Discord
        commandOutputField.setForeground(Color.WHITE);
        commandOutputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(32, 34, 37), 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        commandPanel.add(commandOutputField, BorderLayout.CENTER);
        
        rightPanel.add(commandPanel, BorderLayout.SOUTH);
        
        add(rightPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panneau de contrôle avec tous les paramètres.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        panel.setPreferredSize(new Dimension(300, 600)); // Augmenter la largeur du panneau
        
        // Titre
        JLabel titleLabel = new JLabel("Perlin Noise Generator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Bloc de départ
        JLabel startBlockLabel = new JLabel("Start Bloc:");
        startBlockLabel.setForeground(Color.WHITE);
        startBlockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startBlockLabel);
        
        startBlockSelector = new BlockSelector(textureManager.getAllBlocks(), useTopTexture, this::onBlockSelectionChanged);
        startBlockSelector.setPreferredSize(new Dimension(280, 250)); // Augmenter la largeur
        startBlockSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startBlockSelector);
        panel.add(Box.createVerticalStrut(15));
        
        // Bloc d'arrivée
        JLabel endBlockLabel = new JLabel("End Bloc:");
        endBlockLabel.setForeground(Color.WHITE);
        endBlockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endBlockLabel);
        
        endBlockSelector = new BlockSelector(textureManager.getAllBlocks(), useTopTexture, this::onBlockSelectionChanged);
        endBlockSelector.setPreferredSize(new Dimension(280, 250)); // Augmenter la largeur
        endBlockSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endBlockSelector);
        panel.add(Box.createVerticalStrut(15));
        
        // Nombre de blocs intermédiaires
        JPanel intermediatePanel = new JPanel(new BorderLayout(5, 0));
        intermediatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        intermediatePanel.setBackground(new Color(54, 57, 63)); // Fond Discord
        
        JLabel intermediateLabel = new JLabel("Intermediates Blocs amount:");
        intermediateLabel.setForeground(Color.WHITE);
        intermediatePanel.add(intermediateLabel, BorderLayout.WEST);
        
        intermediateBlocksLabel = new JLabel(String.valueOf(intermediateBlockCount));
        intermediateBlocksLabel.setForeground(Color.WHITE);
        intermediatePanel.add(intermediateBlocksLabel, BorderLayout.EAST);
        
        panel.add(intermediatePanel);
        
        intermediateBlocksSlider = new JSlider(0, 30, intermediateBlockCount);
        intermediateBlocksSlider.setBackground(new Color(54, 57, 63)); // Fond Discord
        intermediateBlocksSlider.setForeground(Color.WHITE);
        intermediateBlocksSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        intermediateBlocksSlider.addChangeListener(e -> {
            intermediateBlockCount = intermediateBlocksSlider.getValue();
            intermediateBlocksLabel.setText(String.valueOf(intermediateBlockCount));
            if (!intermediateBlocksSlider.getValueIsAdjusting()) {
                updatePreview();
            }
        });
        panel.add(intermediateBlocksSlider);
        panel.add(Box.createVerticalStrut(15));
        
        // Échelle du Perlin Noise
        JPanel scalePanel = new JPanel(new BorderLayout(5, 0));
        scalePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scalePanel.setBackground(new Color(54, 57, 63)); // Fond Discord
        
        JLabel scaleLabel = new JLabel("Perlin Size:");
        scaleLabel.setForeground(Color.WHITE);
        scalePanel.add(scaleLabel, BorderLayout.WEST);
        
        perlinScaleLabel = new JLabel(String.valueOf((int)perlinScale));
        perlinScaleLabel.setForeground(Color.WHITE);
        scalePanel.add(perlinScaleLabel, BorderLayout.EAST);
        
        panel.add(scalePanel);
        
        perlinScaleSlider = new JSlider(1, 50, (int)perlinScale);
        perlinScaleSlider.setBackground(new Color(54, 57, 63)); // Fond Discord
        perlinScaleSlider.setForeground(Color.WHITE);
        perlinScaleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        perlinScaleSlider.addChangeListener(e -> {
            perlinScale = perlinScaleSlider.getValue();
            perlinScaleLabel.setText(String.valueOf((int)perlinScale));
            if (!perlinScaleSlider.getValueIsAdjusting()) {
                updatePreview();
            }
        });
        panel.add(perlinScaleSlider);
        panel.add(Box.createVerticalStrut(15));
        
        // Bouton de génération
        generateButton = new JButton("Generate Perlin Noise");
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateButton.setBackground(new Color(114, 137, 218)); // Bleu Discord
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(e -> updatePreview());
        panel.add(generateButton);
        
        return panel;
    }
    
    /**
     * Crée le panneau de prévisualisation.
     */
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (previewImage != null) {
                    g.drawImage(previewImage, 0, 0, this);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                if (previewImage != null) {
                    return new Dimension(previewImage.getWidth(), previewImage.getHeight());
                }
                return new Dimension(512, 512);
            }
        };
        panel.setBackground(new Color(47, 49, 54)); // Fond sombre Discord
        return panel;
    }
    
    /**
     * Gestionnaire de changement de bloc.
     */
    private void onBlockSelectionChanged(Block block) {
        updatePreview();
    }
    
    /**
     * Met à jour la prévisualisation du Perlin Noise.
     */
    private void updatePreview() {
        Block startBlock = startBlockSelector.getSelectedBlock();
        Block endBlock = endBlockSelector.getSelectedBlock();
        
        if (startBlock == null || endBlock == null) {
            return;
        }
        
        // Désactiver le bouton pendant la génération
        generateButton.setEnabled(false);
        generateButton.setText("Génération en cours...");
        
        // Exécuter la génération dans un thread séparé
        executor.submit(() -> {
            try {
                // Générer le gradient de blocs
                int steps = intermediateBlockCount + 2; // +2 pour inclure les blocs de début et de fin
                gradientBlocks = gradientGenerator.generateBlockGradient(startBlock, endBlock, steps, useTopTexture);
                
                // Générer l'image du Perlin Noise
                previewImage = generatePerlinNoiseImage(gradientBlocks, useTopTexture);
                
                // Générer la commande FAWE
                String faweCommand = generateFaweCommand(gradientBlocks);
                
                // Mettre à jour l'UI dans l'EDT
                SwingUtilities.invokeLater(() -> {
                    previewPanel.repaint();
                    commandOutputField.setText(faweCommand);
                    generateButton.setEnabled(true);
                    generateButton.setText("Générer le Perlin Noise");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors de la génération: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                    );
                    generateButton.setEnabled(true);
                    generateButton.setText("Générer le Perlin Noise");
                });
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Génère la commande FAWE pour le Perlin Noise.
     */
    private String generateFaweCommand(List<Block> blocks) {
        // Format: //set #perlin[taille][bloc1,bloc2,...]
        StringBuilder command = new StringBuilder("//set #perlin[");
        command.append((int)perlinScale); // Taille du Perlin
        command.append("][");
        
        // Éliminer les blocs consécutifs identiques
        List<Block> uniqueConsecutiveBlocks = new ArrayList<>();
        Block previousBlock = null;
        
        for (Block block : blocks) {
            if (previousBlock == null || !previousBlock.getName().equals(block.getName())) {
                uniqueConsecutiveBlocks.add(block);
                previousBlock = block;
            }
        }
        
        // Ajouter tous les blocs uniques consécutifs
        String blocksList = uniqueConsecutiveBlocks.stream()
            .map(block -> {
                String name = block.getName();
                // Supprimer le suffixe "_top" s'il existe
                if (name.endsWith("_top")) {
                    return name.substring(0, name.length() - 4);
                }
                return name;
            })
            .collect(Collectors.joining(","));
        command.append(blocksList);
        
        command.append("]");
        return command.toString();
    }
    

    /**
     * Génère l'image du Perlin Noise avec les blocs du gradient.
     * 
     * @param blocks Liste des blocs à utiliser
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @return Image du Perlin Noise
     */
    private BufferedImage generatePerlinNoiseImage(List<Block> blocks, boolean useTopTexture) {
        int blockSize = 16; // Taille d'un bloc en pixels
        int width = PREVIEW_SIZE;
        int height = PREVIEW_SIZE;
        
        BufferedImage image = new BufferedImage(
            width * blockSize,
            height * blockSize,
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g = image.createGraphics();
        
        // Générer un bruit de Perlin
        PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Calculer la valeur de Perlin pour cette position
                double nx = x / (double) width;
                double ny = y / (double) height;
                
                // Inverser l'échelle pour correspondre au comportement de FAWE
                // Plus la valeur est grande, plus les taches sont grandes
                double scale = 51.0 - perlinScale; // Inverser l'échelle (1-50 -> 50-1)
                scale = Math.max(1.0, scale); // Éviter les valeurs trop petites
                
                // Multiplier par 9 pour correspondre à l'échelle de FAWE
                scale = scale / 9.0;
                
                double value = perlinNoise.noise(nx * scale, ny * scale, 0);
                
                // Normaliser entre 0 et 1
                value = (value + 1) / 2;
                
                // Mapper cette valeur à un indice dans notre liste de blocs
                // Utiliser une approche qui garantit que les valeurs extrêmes (0 et 1) 
                // correspondent aux blocs de début et de fin
                int blockIndex;
                
                if (value >= 0.999) {
                    // Forcer le dernier bloc pour les valeurs très proches de 1
                    blockIndex = blocks.size() - 1;
                } else {
                    // Distribution normale pour les autres valeurs
                    blockIndex = (int) (value * blocks.size());
                    
                    // Protection contre les index hors limites
                    blockIndex = Math.max(0, Math.min(blocks.size() - 1, blockIndex));
                }
                
                Block block = blocks.get(blockIndex);
                BufferedImage texture = useTopTexture ? block.getTopTexture() : block.getSideTexture();
                
                if (texture != null) {
                    g.drawImage(texture, x * blockSize, y * blockSize, blockSize, blockSize, null);
                }
            }
        }
        
        g.dispose();
        return image;
    }
    
    /**
     * Classe pour générer du bruit de Perlin.
     * Implémentation adaptée de l'algorithme classique de Perlin Noise.
     */
    private static class PerlinNoise {
        private final int[] permutation = new int[512];
        private final Random random;
        
        public PerlinNoise(long seed) {
            random = new Random(seed);
            
            // Initialiser le tableau de permutation
            for (int i = 0; i < 256; i++) {
                permutation[i] = i;
            }
            
            // Mélanger le tableau
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256);
                int temp = permutation[i];
                permutation[i] = permutation[j];
                permutation[j] = temp;
            }
            
            // Dupliquer pour éviter les calculs modulo
            for (int i = 0; i < 256; i++) {
                permutation[i + 256] = permutation[i];
            }
        }
        
        /**
         * Génère une valeur de bruit de Perlin pour les coordonnées données.
         */
        public double noise(double x, double y, double z) {
            // Trouver les coordonnées de la cellule
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;
            int Z = (int) Math.floor(z) & 255;
            
            // Coordonnées relatives dans la cellule (0 à 1)
            x -= Math.floor(x);
            y -= Math.floor(y);
            z -= Math.floor(z);
            
            // Calcul des courbes de fondu
            double u = fade(x);
            double v = fade(y);
            double w = fade(z);
            
            // Calcul des hachages pour les 8 coins du cube
            int A = permutation[X] + Y;
            int AA = permutation[A] + Z;
            int AB = permutation[A + 1] + Z;
            int B = permutation[X + 1] + Y;
            int BA = permutation[B] + Z;
            int BB = permutation[B + 1] + Z;
            
            // Interpolation trilinéaire des 8 coins
            double result = lerp(w,
                lerp(v,
                    lerp(u, 
                        grad(permutation[AA], x, y, z),
                        grad(permutation[BA], x - 1, y, z)
                    ),
                    lerp(u,
                        grad(permutation[AB], x, y - 1, z),
                        grad(permutation[BB], x - 1, y - 1, z)
                    )
                ),
                lerp(v,
                    lerp(u,
                        grad(permutation[AA + 1], x, y, z - 1),
                        grad(permutation[BA + 1], x - 1, y, z - 1)
                    ),
                    lerp(u,
                        grad(permutation[AB + 1], x, y - 1, z - 1),
                        grad(permutation[BB + 1], x - 1, y - 1, z - 1)
                    )
                )
            );
            
            // Normaliser le résultat entre -1 et 1
            return result;
        }
        
        /**
         * La fonction de fondu pour les coordonnées.
         */
        private double fade(double t) {
            // Fonction de fondu t^3(t(6t-15)+10)
            return t * t * t * (t * (t * 6 - 15) + 10);
        }
        
        /**
         * Interpolation linéaire.
         */
        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }
        
        /**
         * Calcule le produit scalaire entre le vecteur de gradient et le vecteur de position.
         */
        private double grad(int hash, double x, double y, double z) {
            // Convertir les 4 bits inférieurs du hash en 12 vecteurs de gradient
            int h = hash & 15;
            
            // Premier composant basé sur le bit 0
            double u = h < 8 ? x : y;
            
            // Second composant basé sur le bit 1
            double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            
            // Résultat basé sur les bits 2 et 3
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
    
    /**
     * Libère les ressources.
     */
    public void shutdown() {
        executor.shutdown();
    }
}