package skymc.controller;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import skymc.model.PixelArtConverter;
import skymc.util.FileUtils;
import skymc.view.PixelArtView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Contrôleur pour la conversion d'images en pixel art avec des blocs Minecraft.
 * Modifié pour supporter le nouveau système de zoom avec ScrollPane
 */
public class PixelArtController {
    private PixelArtView view;
    private PixelArtConverter model;
    private Stage stage;
    private File lastLoadedFile;
    private BufferedImage lastConvertedImage;

    public PixelArtController(PixelArtView view, PixelArtConverter model, Stage stage) {
        this.view = view;
        this.model = model;
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        // Configuration du bouton de chargement d'image
        view.getLoadImageButton().setOnAction(e -> loadImage());
        
        // Configuration du bouton de conversion
        view.getConvertButton().setOnAction(e -> {
            // Vérifier la taille avant conversion
            int heightInBlocks;
            try {
                heightInBlocks = Integer.parseInt(view.getHeightField().getText());
            } catch (NumberFormatException ex) {
                heightInBlocks = 64; // Valeur par défaut
                view.getHeightField().setText("64");
                convertImage();
                return;
            }
            
            // Afficher l'avertissement si la taille dépasse 512
            if (heightInBlocks > 512) {
                if (view.showLargeRenderWarning()) {
                    convertImage();
                }
            } else {
                convertImage();
            }
        });
        
        // Configuration du bouton de sauvegarde
        view.getSaveButton().setOnAction(e -> saveImage());
        
        // Configuration des boutons de zoom avec limites plus strictes
        view.getZoomInButton().setOnAction(e -> {
            // Limiter le zoom maximum à 5.0 (500%)
            double zoomFactor = Math.min(view.getZoomFactor() * 1.25, 10.0);
            view.setZoomFactor(zoomFactor);
        });
        
        view.getZoomOutButton().setOnAction(e -> {
            // Limiter le zoom minimum à 0.25 (25%)
            double zoomFactor = Math.max(view.getZoomFactor() / 1.25, 0.05);
            view.setZoomFactor(zoomFactor);
        });
    }

    /**
     * Charge une image depuis le système de fichiers.
     */
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir une image");
        
        // Filtres pour les extensions d'image
        FileChooser.ExtensionFilter pngFilter = 
                new FileChooser.ExtensionFilter("Images PNG (*.png)", "*.png");
        FileChooser.ExtensionFilter jpgFilter = 
                new FileChooser.ExtensionFilter("Images JPEG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");
        FileChooser.ExtensionFilter allImagesFilter = 
                new FileChooser.ExtensionFilter("Toutes les images", "*.png", "*.jpg", "*.jpeg");
        
        fileChooser.getExtensionFilters().addAll(allImagesFilter, pngFilter, jpgFilter);
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                // Charger l'image avec JavaFX
                Image image = new Image(file.toURI().toString());
                view.getSourceImageView().setImage(image);
                lastLoadedFile = file;
            } catch (Exception ex) {
                showErrorAlert("Erreur lors du chargement de l'image", ex.getMessage());
            }
        }
    }

    /**
     * Convertit l'image chargée en pixel art.
     */
    private void convertImage() {
        Image sourceImage = view.getSourceImageView().getImage();
        if (sourceImage != null) {
            try {
                // Convertir l'image JavaFX en BufferedImage
                BufferedImage bufferedSource;
                
                // Si on a chargé directement depuis un fichier, utiliser ce chemin pour éviter les problèmes de conversion
                if (lastLoadedFile != null) {
                    bufferedSource = ImageIO.read(lastLoadedFile);
                } else {
                    bufferedSource = FileUtils.toBufferedImage(sourceImage);
                }
                
                // Obtenir la hauteur désirée en blocs
                int heightInBlocks;
                try {
                    heightInBlocks = Integer.parseInt(view.getHeightField().getText());
                } catch (NumberFormatException e) {
                    heightInBlocks = 64; // Valeur par défaut
                    view.getHeightField().setText("64");
                }
                
                // Calculer la largeur proportionnelle en blocs
                double aspectRatio = bufferedSource.getWidth() / (double) bufferedSource.getHeight();
                int widthInBlocks = (int) (heightInBlocks * aspectRatio);
                
                // Définir l'option de texture (dessus ou côté)
                model.setUseTopTexture(view.isTopTextureSelected());
                
                // Convertir en pixel art
                lastConvertedImage = model.convertToPixelArt(bufferedSource, widthInBlocks, heightInBlocks);
                
                // Convertir en image JavaFX et afficher
                Image pixelArt = FileUtils.toFXImage(lastConvertedImage);
                view.getPixelArtView().setImage(pixelArt);
                
                // Réinitialiser le zoom à 1.0 (100%)
                view.setZoomFactor(1.0);
                
            } catch (Exception ex) {
                showErrorAlert("Erreur lors de la conversion", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Sauvegarde l'image pixel art générée.
     */
    private void saveImage() {
        if (lastConvertedImage != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer l'image");
            
            // Filtre pour les fichiers PNG
            FileChooser.ExtensionFilter pngFilter = 
                    new FileChooser.ExtensionFilter("Images PNG (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(pngFilter);
            
            // Suggérer un nom de fichier basé sur l'original
            if (lastLoadedFile != null) {
                String originalName = lastLoadedFile.getName();
                String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
                String suggestedName = baseName + "_pixel_art.png";
                fileChooser.setInitialFileName(suggestedName);
                fileChooser.setInitialDirectory(lastLoadedFile.getParentFile());
            }
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    // S'assurer que le fichier a l'extension .png
                    String filePath = file.getPath();
                    if (!filePath.toLowerCase().endsWith(".png")) {
                        filePath += ".png";
                        file = new File(filePath);
                    }
                    
                    // Sauvegarder l'image
                    ImageIO.write(lastConvertedImage, "png", file);
                } catch (IOException ex) {
                    showErrorAlert("Erreur lors de la sauvegarde", ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'erreur.
     */
    private void showErrorAlert(String header, String content) {
        // Dans une vraie application, vous utiliseriez Alert ou Dialog
        System.err.println(header + ": " + content);
    }
}