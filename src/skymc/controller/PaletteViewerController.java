package skymc.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PaletteViewerController {
    
    private ComboBox<String> paletteSelector;
    private ImageView paletteImageView;
    private ScrollPane scrollPane;
    
    private Image currentImage;
    private String[] paletteNames = {"palette", "palette2", "palette3", "palette4"};
    
    // Setters pour l'injection des composants depuis la vue
    public void setPaletteSelector(ComboBox<String> paletteSelector) {
        this.paletteSelector = paletteSelector;
    }
    
    public void setPaletteImageView(ImageView paletteImageView) {
        this.paletteImageView = paletteImageView;
    }
    
    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
    
    public void initialize() {
        // Initialiser le ComboBox avec les noms des palettes
        paletteSelector.getItems().addAll(paletteNames);
        paletteSelector.setValue("palette3"); // Palette par défaut
        
        // Configurer l'ImageView pour ne pas lisser les pixels
        paletteImageView.setSmooth(false);
        paletteImageView.setPreserveRatio(false);
        
        // Listener pour la sélection de palette
        paletteSelector.setOnAction(e -> loadSelectedPalette());
        
        // Listener pour ajuster le zoom quand la taille du conteneur change
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                adjustZoomToFitWidth();
            }
        });
        
        // Listener pour détecter quand le ScrollPane est prêt et recharger l'image
        scrollPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.getWidth() > 0 && currentImage != null) {
                adjustZoomToFitWidth();
            }
        });
        
        // Charger la palette par défaut
        loadSelectedPalette();
        
        // Forcer un rechargement après que l'interface soit complètement initialisée
        javafx.application.Platform.runLater(() -> {
            javafx.application.Platform.runLater(() -> {
                if (currentImage != null) {
                    adjustZoomToFitWidth();
                }
            });
        });
    }
    
    private void loadSelectedPalette() {
        String selectedPalette = paletteSelector.getValue();
        if (selectedPalette != null) {
            String imagePath = "textures/palettes/" + selectedPalette + ".png";
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    FileInputStream fis = new FileInputStream(imageFile);
                    currentImage = new Image(fis, 0, 0, false, false); // Pas de lissage
                    fis.close();
                    
                    paletteImageView.setImage(currentImage);
                    adjustZoomToFitWidth();
                } else {
                    System.err.println("Fichier non trouvé: " + imagePath);
                    // Créer une image de test si le fichier n'existe pas
                    createTestImage();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                createTestImage();
            }
        }
    }
    
    private void createTestImage() {
        // Créer une image de test pixelisée pour la démonstration
        int width = 64;
        int height = 64;
        WritableImage testImage = new WritableImage(width, height);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Créer un motif coloré pixelisé
                int r = (x * 4) % 256;
                int g = (y * 4) % 256;
                int b = ((x + y) * 2) % 256;
                int color = (0xFF << 24) | (r << 16) | (g << 8) | b;
                testImage.getPixelWriter().setArgb(x, y, color);
            }
        }
        
        currentImage = testImage;
        paletteImageView.setImage(currentImage);
        adjustZoomToFitWidth();
    }
    
    private void adjustZoomToFitWidth() {
        if (currentImage != null && scrollPane.getWidth() > 0) {
            // Calculer le facteur de zoom pour que l'image occupe toute la largeur du conteneur
            double containerWidth = scrollPane.getViewportBounds().getWidth();
            if (containerWidth <= 0 || Double.isNaN(containerWidth)) {
                containerWidth = scrollPane.getWidth() - 20; // Marge pour les scrollbars
            }
            
            // Vérifier que nous avons une largeur valide
            if (containerWidth > 0) {
                double imageWidth = currentImage.getWidth();
                double zoomFactor = containerWidth / imageWidth;
                
                // Appliquer le zoom
                applyZoom(zoomFactor);
            }
        }
    }
    
    private void applyZoom(double zoomFactor) {
        if (currentImage != null) {
            // Pour les petites images et zoom raisonnable, utiliser l'algorithme pixel perfect
            if (currentImage.getWidth() * currentImage.getHeight() * zoomFactor * zoomFactor < 2000000) {
                WritableImage zoomedImage = createPixelPerfectZoom(currentImage, zoomFactor);
                paletteImageView.setImage(zoomedImage);
                paletteImageView.setFitWidth(-1);
                paletteImageView.setFitHeight(-1);
            } else {
                // Pour les grandes images ou gros zoom, utiliser le scaling JavaFX mais optimisé
                paletteImageView.setImage(currentImage);
                paletteImageView.setFitWidth(currentImage.getWidth() * zoomFactor);
                paletteImageView.setFitHeight(currentImage.getHeight() * zoomFactor);
            }
        }
    }
    
    private WritableImage createPixelPerfectZoom(Image sourceImage, double zoomFactor) {
        int sourceWidth = (int) sourceImage.getWidth();
        int sourceHeight = (int) sourceImage.getHeight();
        int zoomedWidth = (int) (sourceWidth * zoomFactor);
        int zoomedHeight = (int) (sourceHeight * zoomFactor);
        
        WritableImage zoomedImage = new WritableImage(zoomedWidth, zoomedHeight);
        var pixelReader = sourceImage.getPixelReader();
        var pixelWriter = zoomedImage.getPixelWriter();
        
        // Optimisation: traiter par blocs plutôt que pixel par pixel
        for (int sourceX = 0; sourceX < sourceWidth; sourceX++) {
            for (int sourceY = 0; sourceY < sourceHeight; sourceY++) {
                int color = pixelReader.getArgb(sourceX, sourceY);
                
                // Dessiner un bloc de pixels pour ce pixel source
                int startX = (int) (sourceX * zoomFactor);
                int startY = (int) (sourceY * zoomFactor);
                int endX = Math.min(zoomedWidth, (int) ((sourceX + 1) * zoomFactor));
                int endY = Math.min(zoomedHeight, (int) ((sourceY + 1) * zoomFactor));
                
                // Remplir le bloc
                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        pixelWriter.setArgb(x, y, color);
                    }
                }
            }
        }
        
        return zoomedImage;
    }
}