import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Classe pour convertir une image en pixel art utilisant des blocs Minecraft.
 */
public class PixelArtConverter {
    private final TextureManager textureManager;
    
    /**
     * Constructeur.
     */
    public PixelArtConverter() {
        this.textureManager = TextureManager.getInstance();
    }
    
    /**
     * Convertit une image en pixel art Minecraft.
     * 
     * @param sourceImage Image source
     * @param targetHeight Hauteur cible en blocs
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @return Image représentant le pixel art
     */
    public BufferedImage convertToPixelArt(BufferedImage sourceImage, int targetHeight, boolean useTopTexture) {
        if (sourceImage == null) {
            return null;
        }
        
        // Redimensionner l'image à la hauteur cible
        double ratio = (double) sourceImage.getWidth() / sourceImage.getHeight();
        int targetWidth = (int) Math.round(targetHeight * ratio);
        
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        
        // Créer la carte des blocs pour chaque pixel
        Block[][] blockMap = new Block[targetHeight][targetWidth];
        
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                Color pixelColor = new Color(resizedImage.getRGB(x, y), true);
                
                // Ignorer les pixels transparents
                if (pixelColor.getAlpha() < 128) {
                    continue;
                }
                
                Block closestBlock = textureManager.findClosestColorBlock(pixelColor, useTopTexture);
                blockMap[y][x] = closestBlock;
            }
        }
        
        // Créer l'image finale du pixel art
        int blockSize = 16; // Taille standard des textures Minecraft
        BufferedImage pixelArt = new BufferedImage(targetWidth * blockSize, targetHeight * blockSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = pixelArt.createGraphics();
        
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                Block block = blockMap[y][x];
                
                if (block != null) {
                    BufferedImage texture = useTopTexture ? block.getTopTexture() : block.getSideTexture();
                    
                    if (texture != null) {
                        g2.drawImage(texture, x * blockSize, y * blockSize, blockSize, blockSize, null);
                    }
                }
            }
        }
        
        g2.dispose();
        return pixelArt;
    }
    
    /**
     * Sauvegarde le pixel art en tant qu'image.
     * 
     * @param pixelArt Image du pixel art
     * @param filePath Chemin du fichier de sortie
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean savePixelArt(BufferedImage pixelArt, String filePath) {
        if (pixelArt == null) {
            return false;
        }
        
        try {
            File outputFile = new File(filePath);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            
            String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
            return ImageIO.write(pixelArt, extension, outputFile);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement du pixel art: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Charge une image depuis un fichier.
     * 
     * @param filePath Chemin du fichier
     * @return L'image chargée ou null en cas d'erreur
     */
    public BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            return null;
        }
    }
}