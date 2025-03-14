import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

public class Bloc {
    private String nom;
    private Color couleur;
    private BufferedImage image;

    // Constructeur modifié pour accepter un BufferedImage
    public Bloc(String nom, String couleurHex, BufferedImage image) {
        this.nom = nom;
        this.couleur = Color.decode(couleurHex); // Convertir HEX en RGB
        this.image = image; // Utiliser l'image passée en paramètre
    }

    public String getNom() {
        return nom;
    }

    public Color getCouleur() {
        return couleur;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public String toString() {
        return nom + " (couleur: #" + String.format("%02X%02X%02X", 
            couleur.getRed(), couleur.getGreen(), couleur.getBlue()) + ")";
    }
}
