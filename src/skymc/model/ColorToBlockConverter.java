package skymc.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Classe responsable de la conversion de couleurs en blocs Minecraft.
 */
public class ColorToBlockConverter {
    
    private TextureManager textureManager;
    private boolean useTopTexture = true;
    private int maxResults = 10;
    
    /**
     * Constructeur du convertisseur.
     */
    public ColorToBlockConverter() {
        this.textureManager = TextureManager.getInstance();
    }
    
    /**
     * Définit si on utilise la texture du dessus ou latérale.
     * 
     * @param useTopTexture true pour utiliser la texture du dessus, false pour la texture latérale
     */
    public void setUseTopTexture(boolean useTopTexture) {
        this.useTopTexture = useTopTexture;
    }
    
    /**
     * Définit le nombre maximum de résultats à retourner.
     * 
     * @param maxResults Nombre maximum de résultats
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    /**
     * Trouve les blocs dont la couleur est la plus proche de la couleur spécifiée.
     * 
     * @param targetColor Couleur cible
     * @return Liste de blocs triés par proximité de couleur
     */
    public List<BlockDistanceResult> findClosestBlocks(Color targetColor) {
        List<BlockDistanceResult> results = new ArrayList<>();
        List<Block> allBlocks = textureManager.getAllBlocks();
        
        for (Block block : allBlocks) {
            Color blockColor = block.getAverageColor(useTopTexture);
            if (blockColor != null) {
                double distance = ColorUtils.colorDistance(targetColor, blockColor);
                results.add(new BlockDistanceResult(block, distance));
            }
        }
        
        // Trier par distance (du plus proche au plus éloigné)
        Collections.sort(results, Comparator.comparingDouble(BlockDistanceResult::getDistance));
        
        // Limiter le nombre de résultats
        if (results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }
        
        return results;
    }
    
    /**
     * Classe interne pour stocker un bloc et sa distance par rapport à la couleur cible.
     */
    public static class BlockDistanceResult {
        private Block block;
        private double distance;
        
        public BlockDistanceResult(Block block, double distance) {
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