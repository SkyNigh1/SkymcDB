package skymc.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable de la génération de gradients de couleurs.
 */
public class GradientGenerator {
    
    /**
     * Génère un gradient de couleurs entre deux blocs.
     * 
     * @param startBlock Bloc de départ
     * @param endBlock Bloc d'arrivée
     * @param steps Nombre d'étapes intermédiaires
     * @param useTopTexture Utiliser la texture du dessus (true) ou latérale (false)
     * @return Liste des blocs formant le gradient
     */
    public List<Block> generateGradient(Block startBlock, Block endBlock, int steps, boolean useTopTexture) {
        List<Block> gradientBlocks = new ArrayList<>();
        
        gradientBlocks.add(startBlock);
        
        if (steps <= 0 || startBlock == endBlock) {
            return gradientBlocks;
        }
        
        Color startColor = startBlock.getAverageColor(useTopTexture);
        Color endColor = endBlock.getAverageColor(useTopTexture);
        
        if (startColor == null) {
            startColor = endColor != null ? endColor : Color.BLACK;
        }
        if (endColor == null) {
            endColor = startColor != null ? startColor : Color.BLACK;
        }
        
        TextureManager textureManager = TextureManager.getInstance();
        
        for (int i = 1; i < steps; i++) {
            float ratio = (float) i / steps;
            
            int r = interpolate(startColor.getRed(), endColor.getRed(), ratio);
            int g = interpolate(startColor.getGreen(), endColor.getGreen(), ratio);
            int b = interpolate(startColor.getBlue(), endColor.getBlue(), ratio);
            
            Color intermediateColor = new Color(r, g, b);
            
            Block closestBlock = textureManager.findClosestColorBlock(intermediateColor, useTopTexture);
            
            if (closestBlock != null) {
                gradientBlocks.add(closestBlock);
            }
        }
        
        gradientBlocks.add(endBlock);
        
        return gradientBlocks;
    }
    
    /**
     * Génère un gradient bilinéaire 2D entre quatre blocs.
     * 
     * @param topLeft Bloc en haut à gauche
     * @param topRight Bloc en haut à droite
     * @param bottomLeft Bloc en bas à gauche
     * @param bottomRight Bloc en bas à droite
     * @param gridSize Taille de la grille (nombre de blocs par côté)
     * @param useTopTexture Utiliser la texture du dessus (true) ou latérale (false)
     * @return Matrice des blocs formant le gradient 2D
     */
    public Block[][] generateBilinearGradient(Block topLeft, Block topRight, Block bottomLeft, Block bottomRight, 
                                             int gridSize, boolean useTopTexture) {
        Block[][] gradient = new Block[gridSize][gridSize];
        TextureManager textureManager = TextureManager.getInstance();
        
        Color c00 = topLeft.getAverageColor(useTopTexture);
        Color c10 = topRight.getAverageColor(useTopTexture);
        Color c01 = bottomLeft.getAverageColor(useTopTexture);
        Color c11 = bottomRight.getAverageColor(useTopTexture);
        
        // Fallback to black if any color is null
        if (c00 == null) c00 = Color.BLACK;
        if (c10 == null) c10 = Color.BLACK;
        if (c01 == null) c01 = Color.BLACK;
        if (c11 == null) c11 = Color.BLACK;

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                float u = (float) x / (gridSize - 1);
                float v = (float) y / (gridSize - 1);
                
                // Bilinear interpolation
                int r = interpolateBilinear(
                    c00.getRed(), c10.getRed(), c01.getRed(), c11.getRed(), u, v);
                int g = interpolateBilinear(
                    c00.getGreen(), c10.getGreen(), c01.getGreen(), c11.getGreen(), u, v);
                int b = interpolateBilinear(
                    c00.getBlue(), c10.getBlue(), c01.getBlue(), c11.getBlue(), u, v);
                
                Color interpolatedColor = new Color(r, g, b);
                gradient[y][x] = textureManager.findClosestColorBlock(interpolatedColor, useTopTexture);
            }
        }
        
        return gradient;
    }
    
    /**
     * Interpole linéairement entre deux valeurs.
     * 
     * @param start Valeur de départ
     * @param end Valeur d'arrivée
     * @param ratio Rapport (entre 0 et 1)
     * @return Valeur interpolée
     */
    private int interpolate(int start, int end, float ratio) {
        return (int) (start + (end - start) * ratio);
    }
    
    /**
     * Interpole bilinéairement entre quatre valeurs.
     * 
     * @param v00 Valeur en haut à gauche
     * @param v10 Valeur en haut à droite
     * @param v01 Valeur en bas à gauche
     * @param v11 Valeur en bas à droite
     * @param u Ratio horizontal (0 à 1)
     * @param v Ratio vertical (0 à 1)
     * @return Valeur interpolée
     */
    private int interpolateBilinear(int v00, int v10, int v01, int v11, float u, float v) {
        float x1 = interpolate(v00, v10, u);
        float x2 = interpolate(v01, v11, u);
        return interpolate((int) x1, (int) x2, v);
    }
}