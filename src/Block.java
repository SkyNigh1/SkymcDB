import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Représente un bloc Minecraft avec ses textures et ses propriétés de couleur.
 */
public class Block {
    private String name;
    private BufferedImage topTexture;
    private BufferedImage sideTexture;
    private Color averageTopColor;
    private Color averageSideColor;

    /**
     * Crée un bloc à partir de son nom et des chemins vers ses textures.
     *
     * @param name Le nom du bloc
     * @param topTexturePath Chemin vers la texture du dessus
     * @param sideTexturePath Chemin vers la texture latérale
     * @throws IOException Si les fichiers de texture ne peuvent pas être lus
     */
    public Block(String name, String topTexturePath, String sideTexturePath) throws IOException {
        this.name = name;

        if (topTexturePath != null) {
            File topFile = new File(topTexturePath);
            if (topFile.exists()) {
                this.topTexture = ImageIO.read(topFile);
                this.averageTopColor = calculateAverageColor(topTexture);
            }
        }

        if (sideTexturePath != null) {
            File sideFile = new File(sideTexturePath);
            if (sideFile.exists()) {
                this.sideTexture = ImageIO.read(sideFile);
                this.averageSideColor = calculateAverageColor(sideTexture);
            }
        }
    }

    /**
     * Calcule la couleur moyenne d'une image.
     *
     * @param image L'image à analyser
     * @return La couleur moyenne de l'image
     */
    private Color calculateAverageColor(BufferedImage image) {
        long totalR = 0, totalG = 0, totalB = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color pixelColor = new Color(rgb, true);

                // Ignore les pixels transparents
                if (pixelColor.getAlpha() > 128) {
                    totalR += pixelColor.getRed();
                    totalG += pixelColor.getGreen();
                    totalB += pixelColor.getBlue();
                    pixelCount++;
                }
            }
        }

        if (pixelCount == 0) return Color.BLACK;

        int avgR = (int) (totalR / pixelCount);
        int avgG = (int) (totalG / pixelCount);
        int avgB = (int) (totalB / pixelCount);

        return new Color(avgR, avgG, avgB);
    }

    /**
     * @return Le nom du bloc
     */
    public String getName() {
        return name;
    }

    /**
     * @return La texture du dessus du bloc
     */
    public BufferedImage getTopTexture() {
        return topTexture;
    }

    /**
     * @return La texture latérale du bloc
     */
    public BufferedImage getSideTexture() {
        return sideTexture;
    }

    public Color getAverageTopColor() {
        if (topTexture == null) {
            return null;
        }
        return averageTopColor;
    }
    
    public Color getAverageSideColor() {
        if (sideTexture == null) {
            return null;
        }
        return averageSideColor;
    }
    

    /**
     * Obtient la couleur moyenne selon le type de texture spécifié.
     *
     * @param useTopTexture true pour utiliser la texture du dessus, false pour la texture latérale
     * @return La couleur moyenne correspondante
     */
    public Color getAverageColor(boolean useTopTexture) {
        return useTopTexture ? averageTopColor : averageSideColor;
    }

    /**
     * Obtient la texture selon le type spécifié.
     *
     * @param useTopTexture true pour obtenir la texture du dessus, false pour la texture latérale
     * @return La texture correspondante
     */
    public BufferedImage getTexture(boolean useTopTexture) {
        return useTopTexture ? topTexture : sideTexture;
    }

    @Override
    public String toString() {
        return name;
    }
}
