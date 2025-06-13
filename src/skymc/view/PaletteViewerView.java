package skymc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import skymc.controller.PaletteViewerController;

public class PaletteViewerView {
    private BorderPane root;
    private ComboBox<String> paletteSelector;
    private ImageView paletteImageView;
    private ScrollPane scrollPane;
    private PaletteViewerController controller;
    
    public PaletteViewerView() {
        root = new BorderPane();
        root.setPadding(new Insets(15));
        
        // Créer les composants
        createComponents();
        layoutComponents();
        
        // Créer et configurer le contrôleur
        controller = new PaletteViewerController();
        controller.setPaletteSelector(paletteSelector);
        controller.setPaletteImageView(paletteImageView);
        controller.setScrollPane(scrollPane);
        controller.initialize();
    }
    
    private void createComponents() {
        // ComboBox pour sélectionner la palette avec style personnalisé
        paletteSelector = new ComboBox<>();
        paletteSelector.setPromptText("Sélectionner une palette");
        paletteSelector.setPrefWidth(200);
        paletteSelector.getStyleClass().add("combo-box");
        
        // ImageView pour afficher la palette
        paletteImageView = new ImageView();
        paletteImageView.setSmooth(false); // Désactiver le lissage pour voir les pixels
        paletteImageView.setPreserveRatio(false); // Éviter les distorsions
        paletteImageView.setCache(false); // Désactiver le cache
        
        // Propriétés CSS pour forcer le rendu pixelisé
        paletteImageView.setStyle("-fx-image-view-type: pixelated;");
        paletteImageView.getStyleClass().add("pixel-perfect");
        
        // ScrollPane pour permettre le défilement quand l'image est zoomée
        scrollPane = new ScrollPane();
        scrollPane.setContent(paletteImageView);
        scrollPane.setFitToWidth(false); // Important: ne pas forcer l'ajustement à la largeur
        scrollPane.setFitToHeight(false); // Important: ne pas forcer l'ajustement à la hauteur
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
    }
    
    private void layoutComponents() {
        // Label pour la palette avec style
        Label paletteLabel = new Label("Palette:");
        paletteLabel.getStyleClass().add("section-label");
        
        // Conteneur pour la sélection de palette
        HBox paletteBox = new HBox(15);
        paletteBox.setAlignment(Pos.CENTER_LEFT);
        paletteBox.getChildren().addAll(paletteLabel, paletteSelector);
        
        // Conteneur principal pour les contrôles avec style de boîte
        VBox controlsContainer = new VBox(10);
        controlsContainer.getChildren().add(paletteBox);
        controlsContainer.setPadding(new Insets(15, 20, 15, 20));
        controlsContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Appliquer le style de boîte similaire au bloc jaune
        controlsContainer.getStyleClass().add("palette-selector-box");
        
        // Conteneur wrapper pour ajouter de l'espacement
        VBox topWrapper = new VBox();
        topWrapper.getChildren().add(controlsContainer);
        topWrapper.setPadding(new Insets(0, 0, 15, 0));
        
        // Placer les contrôles en haut
        root.setTop(topWrapper);
        
        // Placer l'image au centre avec scroll
        root.setCenter(scrollPane);
        
        // S'assurer que le ScrollPane prend tout l'espace disponible
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }
    
    public BorderPane getRoot() {
        return root;
    }
    
    public ComboBox<String> getPaletteSelector() {
        return paletteSelector;
    }
    
    public ImageView getPaletteImageView() {
        return paletteImageView;
    }
    
    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}