import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour générer des dégradés de blocs Minecraft.
 */
public class GradientGenerator {
    private final TextureManager textureManager;

    /**
     * Constructeur.
     */
    public GradientGenerator() {
        this.textureManager = TextureManager.getInstance();
    }

    /**
     * Génère un dégradé de blocs entre deux blocs spécifiés.
     *
     * @param startBlock Bloc de départ
     * @param endBlock Bloc d'arrivée
     * @param steps Nombre d'étapes (incluant les blocs de départ et d'arrivée)
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @return Liste des blocs formant le dégradé
     */
    public List<Block> generateBlockGradient(Block startBlock, Block endBlock, int steps, boolean useTopTexture) {
        if (startBlock == null || endBlock == null || steps < 2) {
            throw new IllegalArgumentException("Les blocs de départ et d'arrivée ne peuvent pas être null et le nombre d'étapes doit être d'au moins 2");
        }

        Color startColor = useTopTexture ? startBlock.getAverageTopColor() : startBlock.getAverageSideColor();
        Color endColor = useTopTexture ? endBlock.getAverageTopColor() : endBlock.getAverageSideColor();

        // Vérifie si les blocs sont compatibles avec la texture sélectionnée
        if (startColor == null) {
            throw new IllegalArgumentException("Le bloc de départ \"" + startBlock.getName() + "\" ne contient pas de couleur moyenne pour la face " + (useTopTexture ? "top" : "side") + ".");
        }

        if (endColor == null) {
            throw new IllegalArgumentException("Le bloc de fin \"" + endBlock.getName() + "\" ne contient pas de couleur moyenne pour la face " + (useTopTexture ? "top" : "side") + ".");
        }

        // Générer les couleurs intermédiaires
        List<Color> gradientColors = ColorUtils.generateGradient(startColor, endColor, steps);
        List<Block> gradientBlocks = new ArrayList<>();

        // Trouver les blocs correspondants pour chaque couleur
        for (int i = 0; i < gradientColors.size(); i++) {
            Color color = gradientColors.get(i);
            Block block;

            if (i == 0) {
                block = startBlock;
            } else if (i == gradientColors.size() - 1) {
                block = endBlock;
            } else {
                block = textureManager.findClosestColorBlock(color, useTopTexture);
            }

            gradientBlocks.add(block);
        }

        return gradientBlocks;
    }

    /**
     * Génère une image représentant le dégradé de blocs.
     *
     * @param blocks Liste de blocs formant le dégradé
     * @param blockSize Taille de chaque bloc dans l'image en pixels
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @return Une image représentant le dégradé
     */
    public BufferedImage createGradientImage(List<Block> blocks, int blockSize, boolean useTopTexture) {
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }

        int width = blocks.size() * blockSize;
        int height = blockSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BufferedImage texture = useTopTexture ? block.getTopTexture() : block.getSideTexture();

            if (texture != null) {
                g.drawImage(texture, i * blockSize, 0, blockSize, blockSize, null);
            }
        }

        g.dispose();
        return image;
    }
}
