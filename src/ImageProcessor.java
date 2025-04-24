import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Classe pour le traitement et la manipulation d'images.
 */
public class ImageProcessor {

    /**
     * Redimensionne une image selon une hauteur spécifiée, en conservant le ratio.
     * 
     * @param originalImage L'image originale
     * @param targetHeight La hauteur cible en pixels
     * @return L'image redimensionnée
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetHeight) {
        if (originalImage == null) return null;
        
        double ratio = (double) targetHeight / originalImage.getHeight();
        int targetWidth = (int) Math.round(originalImage.getWidth() * ratio);
        
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        
        return resizedImage;
    }
    
    /**
     * Charge une image à partir d'un fichier.
     * 
     * @param filePath Le chemin du fichier
     * @return L'image chargée, ou null en cas d'erreur
     */
    public static BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Enregistre une image dans un fichier.
     * 
     * @param image L'image à enregistrer
     * @param filePath Le chemin du fichier
     * @param format Le format d'image (png, jpg, etc.)
     * @return true si l'enregistrement a réussi, false sinon
     */
    public static boolean saveImage(BufferedImage image, String filePath, String format) {
        try {
            File outputFile = new File(filePath);
            // Créer les répertoires parents si nécessaire
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            
            return ImageIO.write(image, format, outputFile);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement de l'image: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtient la couleur d'un pixel spécifique dans une image.
     * 
     * @param image L'image
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return La couleur du pixel
     */
    public static Color getPixelColor(BufferedImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return new Color(0, 0, 0, 0);
        }
        
        int rgb = image.getRGB(x, y);
        return new Color(rgb, true);
    }
    
    /**
     * Crée une image représentant un pixel art en utilisant les blocs Minecraft.
     * 
     * @param source Image source
     * @param blockSize Taille de chaque bloc en pixels dans l'image finale
     * @param useTopTexture true pour utiliser les textures du dessus, false pour les textures latérales
     * @return L'image du pixel art
     */
    public static BufferedImage createPixelArt(BufferedImage source, int blockSize, boolean useTopTexture) {
        if (source == null) return null;
        
        TextureManager textureManager = TextureManager.getInstance();
        int width = source.getWidth();
        int height = source.getHeight();
        
        BufferedImage pixelArt = new BufferedImage(width * blockSize, height * blockSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = pixelArt.createGraphics();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = getPixelColor(source, x, y);
                
                // Ignorer les pixels complètement transparents
                if (pixelColor.getAlpha() == 0) continue;
                
                Block closestBlock = textureManager.findClosestColorBlock(pixelColor, useTopTexture);
                
                if (closestBlock != null) {
                    BufferedImage texture = closestBlock.getTexture(useTopTexture);
                    if (texture != null) {
                        g.drawImage(texture, x * blockSize, y * blockSize, blockSize, blockSize, null);
                    }
                }
            }
        }
        
        g.dispose();
        return pixelArt;
    }
}