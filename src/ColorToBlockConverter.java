import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe pour convertir une couleur en blocs Minecraft correspondants.
 */
public class ColorToBlockConverter {
    private final TextureManager textureManager;
    
    /**
     * Constructeur.
     */
    public ColorToBlockConverter() {
        this.textureManager = TextureManager.getInstance();
    }
    
    /**
     * Trouve les blocs dont la couleur est la plus proche d'une couleur cible.
     * 
     * @param targetColor Couleur cible
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @param maxResults Nombre maximum de résultats à retourner
     * @return Liste des blocs les plus proches, triés par proximité de couleur
     */
    public List<Block> findClosestBlocks(Color targetColor, boolean useTopTexture, int maxResults) {
        if (targetColor == null) {
            return new ArrayList<>();
        }
        
        List<Block> allBlocks = textureManager.getAllBlocks();
        List<BlockDistancePair> distancePairs = new ArrayList<>();
        
        for (Block block : allBlocks) {
            Color blockColor = useTopTexture ? block.getAverageTopColor() : block.getAverageSideColor();
            
            // Si la texture n'existe pas, passer au bloc suivant
            if (blockColor == null) continue;
            
            double distance = ColorUtils.colorDistance(targetColor, blockColor);
            distancePairs.add(new BlockDistancePair(block, distance));
        }
        
        // Trier par distance croissante
        distancePairs.sort(Comparator.comparingDouble(BlockDistancePair::getDistance));
        
        // Prendre les maxResults premiers éléments
        List<Block> result = new ArrayList<>();
        int count = Math.min(distancePairs.size(), maxResults);
        
        for (int i = 0; i < count; i++) {
            result.add(distancePairs.get(i).getBlock());
        }
        
        return result;
    }
    
    /**
     * Classe interne pour associer un bloc et sa distance de couleur.
     */
    private static class BlockDistancePair {
        private final Block block;
        private final double distance;
        
        public BlockDistancePair(Block block, double distance) {
            this.block = block;
            this.distance = distance;
        }
        
        public Block getBlock() {
            return block;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}