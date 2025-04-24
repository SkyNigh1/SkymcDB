import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour les opérations sur les couleurs.
 */
public class ColorUtils {
    
    /**
     * Calcule la distance euclidienne entre deux couleurs dans l'espace RGB.
     * 
     * @param c1 Première couleur
     * @param c2 Deuxième couleur
     * @return La distance entre les deux couleurs
     */
    public static double colorDistance(Color c1, Color c2) {
        int redDiff = c1.getRed() - c2.getRed();
        int greenDiff = c1.getGreen() - c2.getGreen();
        int blueDiff = c1.getBlue() - c2.getBlue();
        
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }
    
    /**
     * Calcule une interpolation linéaire entre deux couleurs.
     * 
     * @param c1 Couleur de départ
     * @param c2 Couleur d'arrivée
     * @param ratio Rapport d'interpolation (0.0 à 1.0)
     * @return La couleur interpolée
     */
    public static Color interpolateColor(Color c1, Color c2, double ratio) {
        int r = (int) Math.round(c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) Math.round(c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) Math.round(c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        
        // Assurer que les valeurs sont dans la plage valide
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));
        
        return new Color(r, g, b);
    }
    
    /**
     * Génère une liste de couleurs formant un dégradé entre deux couleurs.
     * 
     * @param startColor Couleur de départ
     * @param endColor Couleur d'arrivée
     * @param steps Nombre d'étapes (incluant les couleurs de départ et d'arrivée)
     * @return Liste des couleurs du dégradé
     */
    public static List<Color> generateGradient(Color startColor, Color endColor, int steps) {
        List<Color> gradient = new ArrayList<>();
        
        if (steps <= 1) {
            gradient.add(startColor);
            return gradient;
        }
        
        for (int i = 0; i < steps; i++) {
            double ratio = (double) i / (steps - 1);
            gradient.add(interpolateColor(startColor, endColor, ratio));
        }
        
        return gradient;
    }
    
    /**
     * Convertit une couleur RGB en HSV.
     * 
     * @param color Couleur RGB
     * @return Tableau contenant H, S, V (0-360, 0-100, 0-100)
     */
    public static float[] rgbToHsv(Color color) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
        
        // Convertir en format plus lisible (0-360 pour H, 0-100 pour S et V)
        hsv[0] *= 360;
        hsv[1] *= 100;
        hsv[2] *= 100;
        
        return hsv;
    }
    
    /**
     * Convertit des valeurs HSV en couleur RGB.
     * 
     * @param h Teinte (0-360)
     * @param s Saturation (0-100)
     * @param v Valeur (0-100)
     * @return Couleur RGB correspondante
     */
    public static Color hsvToRgb(float h, float s, float v) {
        // Convertir en format HSB (0-1)
        h /= 360;
        s /= 100;
        v /= 100;
        
        return Color.getHSBColor(h, s, v);
    }
}