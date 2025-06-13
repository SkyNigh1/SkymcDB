package skymc.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Classe responsable de la conversion d'images en pixel art avec des blocs Minecraft.
 */
public class PixelArtConverter {
    
    private TextureManager textureManager;
    private boolean useTopTexture = true;
    private int blockSize = 16; // Taille en pixels des blocs dans le rendu final
    
    /**
     * Constructeur du convertisseur.
     */
    public PixelArtConverter() {
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
     * Charge une image depuis un fichier.
     * 
     * @param filePath Chemin du fichier image
     * @return L'image chargée
     * @throws IOException Si une erreur survient lors du chargement
     */
    public BufferedImage loadImage(String filePath) throws IOException {
        return ImageIO.read(new File(filePath));
    }
    
    /**
     * Convertit une image en pixel art fait de blocs Minecraft.
     * 
     * @param sourceImage Image source
     * @param width Largeur désirée en blocs
     * @param height Hauteur désirée en blocs (si -1, calculée proportionnellement)
     * @return Image du pixel art généré
     */
    public BufferedImage convertToPixelArt(BufferedImage sourceImage, int width, int height) {
        // Si la hauteur n'est pas spécifiée, la calculer proportionnellement
        if (height <= 0) {
            height = (int) ((double) sourceImage.getHeight() / sourceImage.getWidth() * width);
        }
        
        // Redimensionner l'image source à la taille désirée
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(sourceImage, 0, 0, width, height, null);
        g.dispose();
        
        // Créer l'image de rendu final avec la taille des blocs
        BufferedImage pixelArt = new BufferedImage(
            width * blockSize,
            height * blockSize,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = pixelArt.createGraphics();
        
        // Pour chaque pixel de l'image redimensionnée
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Obtenir la couleur du pixel
                Color pixelColor = new Color(resizedImage.getRGB(x, y), true);
                
                // Si le pixel est transparent, passer au suivant
                if (pixelColor.getAlpha() < 128) {
                    continue;
                }
                
                // Trouver le bloc le plus proche en couleur
                Block block = textureManager.findClosestColorBlock(pixelColor, useTopTexture);
                
                if (block != null) {
                    // Dessiner la texture du bloc à cet emplacement
                    BufferedImage texture = block.getTexture(useTopTexture);
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
     * Sauvegarde une image dans un fichier.
     * 
     * @param image Image à sauvegarder
     * @param filePath Chemin du fichier de destination
     * @throws IOException Si une erreur survient lors de la sauvegarde
     */
    public void saveImage(BufferedImage image, String filePath) throws IOException {
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        ImageIO.write(image, extension, new File(filePath));
    }
    
    /**
     * Génère la commande FAWE (FastAsyncWorldEdit) pour créer ce pixel art dans Minecraft.
     * 
     * @param pixelArt Image du pixel art
     * @return La commande FAWE
     */
    public String generateFaweCommand(BufferedImage pixelArt) {
        StringBuilder command = new StringBuilder("//set pattern:");
        
        int width = pixelArt.getWidth() / blockSize;
        int height = pixelArt.getHeight() / blockSize;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(pixelArt.getRGB(x * blockSize, y * blockSize), true);
                
                if (pixelColor.getAlpha() < 128) {
                    continue;
                }
                
                Block block = textureManager.findClosestColorBlock(pixelColor, useTopTexture);
                
                if (block != null) {
                    command.append(block.getName()).append(",");
                }
            }
        }
        
        // Supprimer la dernière virgule
        if (command.charAt(command.length() - 1) == ',') {
            command.deleteCharAt(command.length() - 1);
        }
        
        return command.toString();
    }
}