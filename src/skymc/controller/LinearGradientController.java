package skymc.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import skymc.model.Block;
import skymc.model.GradientGenerator;
import skymc.model.TextureManager;
import skymc.view.LinearGradientView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class LinearGradientController {
    private LinearGradientView view;
    private GradientGenerator model;
    private List<Block> blockList;
    private List<Block> currentGradient; // Stocke le dégradé actuel pour référence lors du survol

    public LinearGradientController(LinearGradientView view, GradientGenerator model) {
        this.view = view;
        this.model = model;
        
        // Charger tous les blocs disponibles
        loadBlocks();
        
        // Configurer les événements
        setupEventHandlers();
    }
    
    private void loadBlocks() {
        try {
            // Chargement des blocs
            TextureManager textureManager = TextureManager.getInstance();
            blockList = textureManager.getAllBlocks();
            
            // Mettre à jour les deux grilles avec la liste complète des blocs
            view.getStartBlockGrid().updateBlocks(blockList);
            view.getEndBlockGrid().updateBlocks(blockList);
            
            System.out.println("Loaded " + blockList.size() + " blocks successfully.");
        } catch (Exception e) {
            System.err.println("Error loading blocks: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte à l'utilisateur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger les blocs");
            alert.setContentText("Détails: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void setupEventHandlers() {
        view.getGenerateButton().setOnAction(e -> generateGradient());
    }
    
    private void generateGradient() {
        // Récupérer les blocs sélectionnés
        Block startBlock = view.getStartBlockGrid().getSelectedBlock();
        Block endBlock = view.getEndBlockGrid().getSelectedBlock();
        
        // Vérifier que les blocs sont sélectionnés
        if (startBlock == null || endBlock == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Sélection incomplète");
            alert.setHeaderText("Veuillez sélectionner les blocs de départ et d'arrivée");
            alert.showAndWait();
            return;
        }
        
        // Récupérer le nombre d'étapes
        int steps = view.getStepsSpinner().getValue();
        
        // Récupérer l'option de texture (top ou side)
        boolean useTopTexture = view.isUseTopTextureSelected();
        
        // Utiliser le modèle pour générer le dégradé
        try {
            currentGradient = model.generateGradient(startBlock, endBlock, steps, useTopTexture);
            // Afficher le résultat
            displayGradient(currentGradient);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de générer le dégradé");
            alert.setContentText("Détails: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void displayGradient(List<Block> gradient) {
        // Effacer les résultats précédents
        view.clearResults();
        
        System.out.println("Generated gradient with " + gradient.size() + " blocks");
        
        // Créer le label d'instruction de survol unique
        Label hoverInstructionLabel = new Label("Survolez une texture pour obtenir son nom");
        hoverInstructionLabel.getStyleClass().add("hover-gradient");
        
        // Ajouter le label d'instruction au conteneur de résultats
        view.getLabelResultPane().getChildren().add(hoverInstructionLabel);
        
        // Stocker la référence au label d'instruction pour les mises à jour
        view.setHoverInstructionLabel(hoverInstructionLabel);
        
        // Nouvelle méthode d'affichage avec textures collées
        for (int i = 0; i < gradient.size(); i++) {
            Block block = gradient.get(i);
            try {
                // Récupérer la texture selon l'option sélectionnée
                BufferedImage texture = block.getTexture(view.isUseTopTextureSelected());
                
                if (texture != null) {
                    // Convertir la texture en Image JavaFX
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(texture, "png", outputStream);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    Image fxImage = new Image(inputStream, 48, 48, false, false);
                    
                    // Créer un ImageView pour afficher l'image
                    ImageView imageView = new ImageView(fxImage);
                    imageView.setFitWidth(48);
                    imageView.setFitHeight(48);
                    
                    // Désactiver le lissage pour conserver l'aspect pixelisé
                    imageView.setSmooth(false);
                    imageView.setPreserveRatio(false);
                    
                    // Ajouter les événements de survol sur l'ImageView
                    final Block currentBlock = block; // Bloc final pour utilisation dans les lambda
                    imageView.setOnMouseEntered(e -> {
                        // Mettre à jour le label d'instruction avec le nom du bloc survolé
                        hoverInstructionLabel.setText(currentBlock.getName());
                        // Ajouter une légère échelle pour indiquer le survol
                        imageView.setScaleX(1.1);
                        imageView.setScaleY(1.1);
                    });
                    
                    imageView.setOnMouseExited(e -> {
                        // Rétablir le texte d'instruction original
                        hoverInstructionLabel.setText("Survolez une texture pour obtenir son nom");
                        // Rétablir l'échelle normale
                        imageView.setScaleX(1.0);
                        imageView.setScaleY(1.0);
                    });
                    
                    // Ajouter la texture directement au conteneur d'images (collées)
                    view.getImageResultPane().add(imageView, i, 0);
                }
            } catch (Exception e) {
                System.err.println("Failed to display block " + block.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}