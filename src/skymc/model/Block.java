package skymc.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Représente un bloc Minecraft avec ses textures et couleurs.
 */
public class Block {
    private String name;
    private BufferedImage topTexture;
    private BufferedImage sideTexture;
    private Color averageTopColor;
    private Color averageSideColor;
    private String topTexturePath;
    private String sideTexturePath;

    /**
     * Constructeur de bloc.
     * 
     * @param name Nom du bloc
     * @param topTexturePath Chemin de la texture du dessus (peut être null)
     * @param sideTexturePath Chemin de la texture latérale (peut être null)
     * @throws IOException Si une erreur survient lors du chargement des textures
     */
    public Block(String name, String topTexturePath, String sideTexturePath) throws IOException {
        this.name = name;
        this.topTexturePath = topTexturePath;
        this.sideTexturePath = sideTexturePath;
        
        if (topTexturePath != null) {
            File topFile = new File(topTexturePath);
            if (topFile.exists()) {
                this.topTexture = ImageIO.read(topFile);
                this.averageTopColor = this.calculateAverageColor(this.topTexture);
            }
        }

        if (sideTexturePath != null) {
            File sideFile = new File(sideTexturePath);
            if (sideFile.exists()) {
                this.sideTexture = ImageIO.read(sideFile);
                this.averageSideColor = this.calculateAverageColor(this.sideTexture);
            }
        }
    }

    /**
     * Calcule la couleur moyenne d'une texture.
     * 
     * @param image L'image à analyser
     * @return La couleur moyenne de l'image
     */
    private Color calculateAverageColor(BufferedImage image) {
        long sumRed = 0;
        long sumGreen = 0;
        long sumBlue = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                
                // On ne prend en compte que les pixels non transparents
                if (color.getAlpha() > 128) {
                    sumRed += color.getRed();
                    sumGreen += color.getGreen();
                    sumBlue += color.getBlue();
                    pixelCount++;
                }
            }
        }

        if (pixelCount == 0) {
            return Color.BLACK;
        }

        int avgRed = (int) (sumRed / pixelCount);
        int avgGreen = (int) (sumGreen / pixelCount);
        int avgBlue = (int) (sumBlue / pixelCount);

        return new Color(avgRed, avgGreen, avgBlue);
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public BufferedImage getTopTexture() {
        return this.topTexture;
    }

    public BufferedImage getSideTexture() {
        return this.sideTexture;
    }

    public Color getAverageTopColor() {
        return this.topTexture == null ? null : this.averageTopColor;
    }

    public Color getAverageSideColor() {
        return this.sideTexture == null ? null : this.averageSideColor;
    }

    public Color getAverageColor(boolean useTopTexture) {
        return useTopTexture ? this.averageTopColor : this.averageSideColor;
    }

    public BufferedImage getTexture(boolean useTopTexture) {
        return useTopTexture ? this.topTexture : this.sideTexture;
    }

    public String getTopTexturePath() {
        return this.topTexturePath;
    }

    public String getSideTexturePath() {
        return this.sideTexturePath;
    }

    @Override
    public String toString() {
        return this.name;
    }
}