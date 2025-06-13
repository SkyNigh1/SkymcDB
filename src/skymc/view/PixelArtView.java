package skymc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Alert.AlertType;

/**
 * Vue pour la conversion d'images en pixel art avec des blocs Minecraft.
 * Style conforme à la version 1.4 avec l'agencement amélioré et problèmes de zoom résolus
 */
public class PixelArtView {
    private BorderPane root;
    private ImageView sourceImageView;
    private ImageView pixelArtView;
    private Button loadImageButton;
    private Button convertButton;
    private Button saveButton;
    private TextField heightField;
    private ToggleGroup textureGroup;
    private RadioButton topTextureRadio;
    private RadioButton sideTextureRadio;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Label zoomLabel;
    private double zoomFactor = 1.0;
    private ScrollPane pixelArtScrollPane;
    
    public PixelArtView() {
        createView();
        applyStyles();
        configureHeightFieldLimit();
    }
    
    private void createView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        
        // Créer le panneau de gauche pour les contrôles dans un bloc
        VBox leftPanel = createControlsPanel();
        
        // Conteneur principal pour les images
        HBox imagesContainer = new HBox(10);
        imagesContainer.getStyleClass().add("images-container");
        
        // Créer le panneau central avec l'image source (1/3 de l'espace)
        VBox sourcePanel = createSourcePanel();
        HBox.setHgrow(sourcePanel, Priority.ALWAYS);
        sourcePanel.setMaxWidth(Double.MAX_VALUE);
        
        // Créer le panneau de droite pour le pixel art (2/3 de l'espace)
        VBox pixelArtPanel = createPixelArtPanel();
        HBox.setHgrow(pixelArtPanel, Priority.ALWAYS);
        pixelArtPanel.setMaxWidth(Double.MAX_VALUE);
        pixelArtPanel.setPrefWidth(2 * sourcePanel.getPrefWidth());
        
        // Ajouter les panneaux d'images au conteneur d'images
        imagesContainer.getChildren().addAll(sourcePanel, pixelArtPanel);
        VBox.setVgrow(imagesContainer, Priority.ALWAYS);
        
        // Utiliser un VBox comme conteneur principal pour maximiser la hauteur
        VBox mainContent = new VBox(10);
        mainContent.getChildren().add(imagesContainer);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setFillWidth(true);
        VBox.setVgrow(imagesContainer, Priority.ALWAYS);
        
        // Ajouter les panneaux au layout principal
        BorderPane contentPane = new BorderPane();
        contentPane.setLeft(leftPanel);
        contentPane.setCenter(mainContent);
        contentPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        
        root.setCenter(contentPane);
    }
    
    private VBox createControlsPanel() {
        // Créer un conteneur pour les contrôles avec un style de bloc
        VBox controlsBox = new VBox(20);
        controlsBox.getStyleClass().addAll("LinearContainer", "control-block");
        controlsBox.setPrefWidth(180);
        controlsBox.setPadding(new Insets(15));
        controlsBox.setStyle("-fx-background-color: rgba(20, 20, 20, 0.4); -fx-border-color: rgb(255, 255, 255); -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        
        Label controlsLabel = new Label("CONTROLS");
        controlsLabel.getStyleClass().add("section-label");
        
        loadImageButton = new Button("Load an image");
        loadImageButton.getStyleClass().add("generate-button");
        loadImageButton.setMaxWidth(Double.MAX_VALUE);
        
        // Options de texture
        Label optionsLabel = new Label("OPTIONS");
        optionsLabel.getStyleClass().add("section-label");
        optionsLabel.setPadding(new Insets(10, 0, 5, 0));
        
        textureGroup = new ToggleGroup();
        
        topTextureRadio = new RadioButton("Use the top texture");
        topTextureRadio.setToggleGroup(textureGroup);
        topTextureRadio.setSelected(true);
        topTextureRadio.getStyleClass().add("texture-radio");
        
        sideTextureRadio = new RadioButton("Use the side texture");
        sideTextureRadio.setToggleGroup(textureGroup);
        sideTextureRadio.getStyleClass().add("texture-radio");
        
        // Hauteur en blocs
        HBox heightBox = new HBox(10);
        heightBox.setAlignment(Pos.CENTER_LEFT);
        
        Label heightLabel = new Label("Height (in blocks):");
        heightLabel.getStyleClass().add("section-label");
        
        heightField = new TextField("64");
        heightField.setPrefWidth(60);
        heightField.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
        
        heightBox.getChildren().addAll(heightLabel, heightField);
        
        // Boutons de conversion et sauvegarde
        convertButton = new Button("Convert to pixel art");
        convertButton.getStyleClass().add("generate-button");
        convertButton.setMaxWidth(Double.MAX_VALUE);
        
        saveButton = new Button("Save the result");
        saveButton.getStyleClass().add("generate-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        
        // Ajouter tous les éléments
        controlsBox.getChildren().addAll(
                controlsLabel, 
                loadImageButton, 
                optionsLabel, 
                topTextureRadio, 
                sideTextureRadio, 
                heightBox, 
                convertButton, 
                saveButton);
        
        return controlsBox;
    }
    
    private VBox createSourcePanel() {
        VBox sourceBox = new VBox(10);
        sourceBox.setPadding(new Insets(0, 10, 0, 10));
        sourceBox.setPrefWidth(300); // 1/3 de l'espace
        
        Label titleLabel = new Label("IMAGE SOURCE");
        titleLabel.getStyleClass().add("section-label");
        titleLabel.setAlignment(Pos.CENTER);
        
        // Panneau pour l'image source avec hauteur fixe pour aligner avec le panneau pixel art
        StackPane sourceImagePane = new StackPane();
        sourceImagePane.setMinHeight(800);
        sourceImagePane.setMaxHeight(800); // Hauteur fixe pour aligner avec l'autre panneau
        sourceImagePane.setStyle("-fx-background-color: rgba(20, 20, 20, 0.3); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1px;");
        
        sourceImageView = new ImageView();
        sourceImageView.setPreserveRatio(true);
        sourceImageView.setFitWidth(280);
        sourceImageView.setFitHeight(580);
        
        sourceImagePane.getChildren().add(sourceImageView);
        
        sourceBox.getChildren().addAll(titleLabel, sourceImagePane);
        VBox.setVgrow(sourceImagePane, Priority.ALWAYS);
        return sourceBox;
    }
    
    private VBox createPixelArtPanel() {
        VBox pixelArtBox = new VBox(10);
        pixelArtBox.setPadding(new Insets(0, 10, 0, 10));
        pixelArtBox.setPrefWidth(800); // 2/3 de l'espace
        
        Label titleLabel = new Label("PIXEL ART");
        titleLabel.getStyleClass().add("section-label");
        titleLabel.setAlignment(Pos.CENTER);
        
        // Conteneur pour le pixel art avec clipping
        StackPane pixelArtPane = new StackPane();
        pixelArtPane.setMinHeight(800);
        pixelArtPane.setMaxHeight(800); // Hauteur fixe pour aligner avec l'autre panneau
        pixelArtPane.setStyle("-fx-background-color: rgba(20, 20, 20, 0.3); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1px;");
        
        // Utiliser ScrollPane pour gérer le défilement lors du zoom
        pixelArtScrollPane = new ScrollPane();
        pixelArtScrollPane.setFitToWidth(true);
        pixelArtScrollPane.setFitToHeight(true);
        pixelArtScrollPane.setPannable(true); // Permettre le déplacement à la souris
        pixelArtScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Utiliser un Group pour contenir l'ImageView du pixel art
        Group pixelArtGroup = new Group();
        
        pixelArtView = new ImageView();
        pixelArtView.setPreserveRatio(true);
        pixelArtView.setSmooth(false); // Désactiver le lissage pour un rendu pixel parfait
        pixelArtView.getStyleClass().add("pixel-perfect");
        
        pixelArtGroup.getChildren().add(pixelArtView);
        pixelArtScrollPane.setContent(pixelArtGroup);
        pixelArtPane.getChildren().add(pixelArtScrollPane);
        
        // Contrôles de zoom
        HBox zoomControls = new HBox(10);
        zoomControls.setAlignment(Pos.CENTER_RIGHT);
        
        zoomOutButton = new Button("-");
        zoomOutButton.setStyle("-fx-background-color: rgba(20, 68, 50, 0.7); -fx-text-fill: white;");
        
        zoomLabel = new Label("100%");
        zoomLabel.setStyle("-fx-text-fill: white;");
        
        zoomInButton = new Button("+");
        zoomInButton.setStyle("-fx-background-color: rgba(20, 68, 50, 0.7); -fx-text-fill: white;");
        
        zoomControls.getChildren().addAll(zoomOutButton, zoomLabel, zoomInButton);
        
        pixelArtBox.getChildren().addAll(titleLabel, pixelArtPane, zoomControls);
        VBox.setVgrow(pixelArtPane, Priority.ALWAYS);
        return pixelArtBox;
    }
    
    private void configureHeightFieldLimit() {
        // Limitez la hauteur des blocs à 2048 maximum
        heightField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    // Si l'entrée n'est pas numérique, remplacer par une chaîne vide
                    heightField.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (!newValue.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newValue);
                        if (value > 512) {
                            // Si la valeur dépasse 2048, la limiter à 2048
                            heightField.setText("512");
                        }
                    } catch (NumberFormatException e) {
                        heightField.setText(oldValue);
                    }
                }
            }
        });
    }
    
    /**
     * Affiche une boîte de dialogue d'avertissement pour les rendus de grande taille.
     * @return true si l'utilisateur choisit de continuer, false sinon
     */
    public boolean showLargeRenderWarning() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Attention");
        alert.setHeaderText("Rendu de grande taille");
        alert.setContentText("Attention, un rendu supérieur à 512 blocs peut prendre un certain temps. Voulez-vous continuer ?");

        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType continueButton = new ButtonType("J'ai compris", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(cancelButton, continueButton);

        return alert.showAndWait()
                .filter(buttonType -> buttonType == continueButton)
                .isPresent();
    }
    
    private void applyStyles() {
        // Application du style global
        root.setStyle("-fx-background-color: rgba(30, 30, 30, 0.1);");
        
        // Ajouter des styles supplémentaires pour le conteneur principal
        root.setPrefHeight(800); // Utiliser plus de hauteur
    }
    
    // Getters
    public BorderPane getRoot() {
        return root;
    }
    
    public ImageView getSourceImageView() {
        return sourceImageView;
    }
    
    public ImageView getPixelArtView() {
        return pixelArtView;
    }
    
    public Button getLoadImageButton() {
        return loadImageButton;
    }
    
    public Button getConvertButton() {
        return convertButton;
    }
    
    public Button getSaveButton() {
        return saveButton;
    }
    
    public TextField getHeightField() {
        return heightField;
    }
    
    public boolean isTopTextureSelected() {
        return topTextureRadio.isSelected();
    }
    
    public Button getZoomInButton() {
        return zoomInButton;
    }
    
    public Button getZoomOutButton() {
        return zoomOutButton;
    }
    
    public double getZoomFactor() {
        return zoomFactor;
    }
    
    public void setZoomFactor(double factor) {
        this.zoomFactor = factor;
        zoomLabel.setText(Math.round(factor * 100) + "%");
        
        // Appliquer le zoom à l'image sans utiliser scale
        // À la place, on ajuste la taille de l'image directement
        double baseWidth = pixelArtView.getImage() != null ? pixelArtView.getImage().getWidth() : 0;
        double baseHeight = pixelArtView.getImage() != null ? pixelArtView.getImage().getHeight() : 0;
        
        pixelArtView.setFitWidth(baseWidth * factor);
        pixelArtView.setFitHeight(baseHeight * factor);
    }
}