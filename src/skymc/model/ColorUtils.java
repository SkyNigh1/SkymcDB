package skymc.model;

import java.awt.Color;

/**
 * Classe utilitaire pour les opérations sur les couleurs.
 */
public class ColorUtils {
    
    /**
     * Calcule la distance entre deux couleurs.
     * On utilise la distance euclidienne dans l'espace RGB.
     * 
     * @param c1 Première couleur
     * @param c2 Deuxième couleur
     * @return La distance entre les deux couleurs
     */
    public static double colorDistance(Color c1, Color c2) {
        if (c1 == null || c2 == null) {
            return Double.MAX_VALUE;
        }
        
        int redDiff = c1.getRed() - c2.getRed();
        int greenDiff = c1.getGreen() - c2.getGreen();
        int blueDiff = c1.getBlue() - c2.getBlue();
        
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }
    
    /**
     * Convertit une couleur Java AWT en couleur JavaFX.
     * 
     * @param awtColor Couleur Java AWT
     * @return Couleur JavaFX équivalente
     */
    public static javafx.scene.paint.Color awtToFX(Color awtColor) {
        return javafx.scene.paint.Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
    }
    
    /**
     * Convertit une couleur JavaFX en couleur Java AWT.
     * 
     * @param fxColor Couleur JavaFX
     * @return Couleur Java AWT équivalente
     */
    public static Color fxToAWT(javafx.scene.paint.Color fxColor) {
        return new Color(
            (float) fxColor.getRed(),
            (float) fxColor.getGreen(),
            (float) fxColor.getBlue(),
            (float) fxColor.getOpacity()
        );
    }
    
    /**
     * Convertit une couleur hexadécimale en couleur JavaFX.
     * 
     * @param hexColor Code couleur hexadécimal (format: #RRGGBB)
     * @return Couleur JavaFX correspondante
     */
    public static javafx.scene.paint.Color hexToFXColor(String hexColor) {
        return javafx.scene.paint.Color.web(hexColor);
    }
    
    /**
     * Convertit une couleur JavaFX en code hexadécimal.
     * 
     * @param color Couleur JavaFX
     * @return Code hexadécimal (format: #RRGGBB)
     */
    public static String fxColorToHex(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
}