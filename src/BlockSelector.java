import javax.swing.*;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.io.IOException;


/**
 * Composant d'interface utilisateur pour la sélection d'un bloc Minecraft.
 */
public class BlockSelector extends JPanel {
    private final JTextField searchField;
    private final JPanel blocksPanel;
    private final JScrollPane scrollPane;
    private final List<Block> blocks;
    private final boolean useTopTexture;
    private Block selectedBlock;
    private final Consumer<Block> onBlockSelected;
    private JPanel selectedPanel = null;


    

    
    /**
     * Constructeur.
     * 
     * @param blocks Liste des blocs disponibles
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @param onBlockSelected Callback appelé lorsqu'un bloc est sélectionné
     */
    public BlockSelector(List<Block> blocks, boolean useTopTexture, Consumer<Block> onBlockSelected) {
        this.blocks = new ArrayList<>(blocks);
        this.useTopTexture = useTopTexture;
        this.onBlockSelected = onBlockSelected;
    
        setLayout(new BorderLayout());
    
        // Composants
        searchField = new JTextField();
        searchField.setToolTipText("Rechercher un bloc...");
        add(searchField, BorderLayout.NORTH);
    
        blocksPanel = new JPanel();
        blocksPanel.setLayout(new GridLayout(0, 5, 5, 5)); // 5 colonnes
    
        scrollPane = new JScrollPane(blocksPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    
        // --- Thème sombre façon Discord ---
        Color bgColor = new Color(54, 57, 63);         // fond général
        Color blockColor = new Color(64, 68, 75);      // fond des blocs
        Color borderColor = new Color(114, 137, 218);  // bleu Discord
        Color textColor = Color.WHITE;
        Font uiFont = new Font("Segoe UI", Font.PLAIN, 12);
    
        // Appliquer les styles
        setBackground(bgColor);
        blocksPanel.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        searchField.setBackground(blockColor);
        searchField.setForeground(textColor);
        searchField.setCaretColor(textColor);
        searchField.setFont(uiFont);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
        // Remplir les blocs
        populateBlocksPanel(blocks);
    
        // Listener du champ de recherche
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterBlocks(); }
            @Override public void removeUpdate(DocumentEvent e) { filterBlocks(); }
            @Override public void changedUpdate(DocumentEvent e) { filterBlocks(); }
        });
    }
    
    
    /**
     * Filtre les blocs selon le texte de recherche.
     */
    private void filterBlocks() {
        String searchText = searchField.getText().toLowerCase();
        
        List<Block> filteredBlocks = new ArrayList<>();
        for (Block block : blocks) {
            if (block.getName().toLowerCase().contains(searchText)) {
                filteredBlocks.add(block);
            }
        }
        
        populateBlocksPanel(filteredBlocks);
    }
    
    /**
     * Remplit le panel avec les blocs filtrés.
     * 
     * @param filteredBlocks Liste des blocs à afficher
     */
    private void populateBlocksPanel(List<Block> filteredBlocks) {
        blocksPanel.removeAll();
        
        for (Block block : filteredBlocks) {
            JPanel blockPanel = createBlockPanel(block);
            blocksPanel.add(blockPanel);
        }
        
        blocksPanel.revalidate();
        blocksPanel.repaint();
    }
    
    /**
     * Crée un panel pour représenter un bloc.
     * 
     * @param block Le bloc à représenter
     * @return Le panel créé
     */
    private JPanel createBlockPanel(Block block) {
        // Constantes pour réduire la duplication
        final Color BG_COLOR = new Color(64, 68, 75);
        final Color BORDER_COLOR = new Color(32, 34, 37);
        final Color SELECTED_BORDER_COLOR = new Color(114, 137, 218);
        final int TEXTURE_SIZE = 32;
        
        // Création du panel avec style prédéfini
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        panel.setBackground(BG_COLOR);
        panel.setPreferredSize(new Dimension(64, 64));
        
        // Préparation du label pour l'image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBackground(BG_COLOR);
        
        // Chargement de la texture avec fallback
        loadTexture(block, imageLabel, TEXTURE_SIZE);
        
        // Configuration du label du nom
        JLabel nameLabel = new JLabel(block.getName(), JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Assembler le panel
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);
        
        // Gestion de la sélection
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedBlock = block;
                onBlockSelected.accept(block);
                
                // Mise à jour des bordures
                if (selectedPanel != null) {
                    selectedPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
                }
                
                panel.setBorder(BorderFactory.createLineBorder(SELECTED_BORDER_COLOR, 2, true));
                selectedPanel = panel;
            }
        });
        
        return panel;
    }
    
    /**
     * Charge la texture appropriée pour un bloc et l'applique au label
     * 
     * @param block Le bloc dont on veut la texture
     * @param label Le label où afficher la texture
     * @param size La taille souhaitée pour la texture
     */
    private void loadTexture(Block block, JLabel label, int size) {
        // Essayer d'abord d'obtenir la texture à partir de l'objet Block
        java.awt.image.BufferedImage texture = useTopTexture ? block.getTopTexture() : block.getSideTexture();
        
        // Si la texture n'est pas disponible dans l'objet, essayer de la charger depuis un fichier
        if (texture == null) {
            // Ordre de priorité: side, puis top
            String[] paths = {
                "textures/side/" + block.getName() + ".png",
                "textures/top/" + block.getName() + ".png"
            };
            
            for (String path : paths) {
                File textureFile = new File(path);
                if (textureFile.exists()) {
                    try {
                        texture = ImageIO.read(textureFile);
                        break; // On a trouvé une texture, on sort de la boucle
                    } catch (IOException e) {
                        System.out.println("Erreur lors du chargement de la texture " + path + " pour: " + block.getName());
                        System.out.println("Chemin absolu: " + textureFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        // Appliquer la texture si disponible
        if (texture != null) {
            ImageIcon icon = new ImageIcon(texture.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
            label.setIcon(icon);
        }
    }
    
    /**
     * @return Le bloc actuellement sélectionné
     */
    public Block getSelectedBlock() {
        return selectedBlock;
    }
    
    /**
     * Définit le bloc sélectionné.
     * 
     * @param block Le bloc à sélectionner
     */
    public void setSelectedBlock(Block block) {
        this.selectedBlock = block;
        
        // Actualiser l'affichage
        populateBlocksPanel(blocks);
    }
}