import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère le chargement et le stockage des textures de blocs Minecraft.
 */
public class TextureManager {
    private static final String TOP_TEXTURE_DIR = "textures/top/";
    private static final String SIDE_TEXTURE_DIR = "textures/side/";

    private final Map<String, Block> blockMap = new HashMap<>();
    private final List<Block> blocks = new ArrayList<>();

    private static TextureManager instance;

    /**
     * Obtient l'instance unique du TextureManager.
     *
     * @return L'instance de TextureManager
     */
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    /**
     * Constructeur privé pour le singleton.
     */
    private TextureManager() {
        loadTextures();
    }

    /**
     * Charge toutes les textures des blocs.
     */
    public void loadTextures() {
        blocks.clear();
        blockMap.clear();

        File topDir = new File(TOP_TEXTURE_DIR);
        File sideDir = new File(SIDE_TEXTURE_DIR);

        // Créer les répertoires s'ils n'existent pas
        if (!topDir.exists()) {
            topDir.mkdirs();
        }
        if (!sideDir.exists()) {
            sideDir.mkdirs();
        }

        // Obtenir tous les fichiers du répertoire top
        File[] topFiles = topDir.listFiles((dir, name) -> name.endsWith(".png"));
        File[] sideFiles = sideDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        // Vérification et ajout des textures 'top'
        if (topFiles != null) {
            for (File topFile : topFiles) {
                String blockName = topFile.getName().replace(".png", "");
                String topPath = topFile.getPath();
                String sidePath = SIDE_TEXTURE_DIR + topFile.getName();

                try {
                    Block block = new Block(blockName, topPath, new File(sidePath).exists() ? sidePath : null);
                    blocks.add(block);
                    blockMap.put(blockName, block);
                } catch (IOException e) {
                    System.err.println("Erreur lors du chargement de la texture pour " + blockName + ": " + e.getMessage());
                }
            }
        }

        // Vérification et ajout des textures 'side' uniquement
        if (sideFiles != null) {
            for (File sideFile : sideFiles) {
                String blockName = sideFile.getName().replace(".png", "");


                // Si le bloc n'a pas déjà été ajouté (ne doit pas exister déjà dans blockMap)
                if (!blockMap.containsKey(blockName)) {
                    String sidePath = sideFile.getPath();

                    // Ajout uniquement si le bloc est coté 'side' et pas en top
                    try {
                        Block block = new Block(blockName, null, sidePath);
                        blocks.add(block);
                        blockMap.put(blockName, block);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la texture pour " + blockName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * @return Liste de tous les blocs chargés
     */
    public List<Block> getAllBlocks() {
        return new ArrayList<>(blocks);
    }

    /**
     * Obtient un bloc par son nom.
     *
     * @param name Nom du bloc
     * @return Le bloc correspondant, ou null s'il n'existe pas
     */
    public Block getBlockByName(String name) {
        return blockMap.get(name);
    }

    /**
     * Trouve le bloc qui correspond le mieux à une couleur donnée.
     *
     * @param targetColor Couleur cible
     * @param useTopTexture true pour utiliser la texture du dessus, false pour la texture latérale
     * @return Le bloc dont la couleur est la plus proche
     */
    public Block findClosestColorBlock(java.awt.Color targetColor, boolean useTopTexture) {
        if (blocks.isEmpty()) {
            return null;
        }

        Block closestBlock = null;
        double minDistance = Double.MAX_VALUE;

        for (Block block : blocks) {
            java.awt.Color blockColor = useTopTexture ? block.getAverageTopColor() : block.getAverageSideColor();

            // Si la texture n'existe pas, passer au bloc suivant
            if (blockColor == null) continue;

            double distance = ColorUtils.colorDistance(targetColor, blockColor);

            if (distance < minDistance) {
                minDistance = distance;
                closestBlock = block;
            }
        }

        return closestBlock;
    }
}
