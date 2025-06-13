package skymc.view.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.geometry.Insets;
import skymc.model.Block;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BlockGridView {
    private final ScrollPane root;
    private final GridPane grid;
    private List<Block> allBlocks; // Pour stocker tous les blocs
    private TextField searchField; // Champ de recherche
    private VBox contentContainer; // Conteneur principal incluant la barre de recherche et la grille
    
    // Utilisation de Maps statiques pour stocker les sélections et instances
    private static final Map<String, VBox> selectedContainers = new HashMap<>();
    private static final Map<String, Block> selectedBlocks = new HashMap<>();
    private static final Map<String, BlockGridView> gridInstances = new HashMap<>();
    
    // Identifiant unique pour cette grille
    private final String gridId;
    
    public BlockGridView(String gridId) {
        this.gridId = gridId;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        // Créer le champ de recherche avec le style approprié
        searchField = new TextField();
        searchField.setPromptText("Recherche du bloc");
        searchField.getStyleClass().add("block-search-field");
        
        // Ajouter un listener pour filtrer les blocs quand le texte change
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBlocks(newValue);
        });
        
        // Créer un conteneur pour le champ de recherche et la grille
        contentContainer = new VBox(10, searchField, grid);
        
        root = new ScrollPane(contentContainer);
        root.setFitToWidth(true);
        root.setFitToHeight(true);
        gridInstances.put(gridId, this); // Enregistrer l'instance

        // Ajouter un écouteur pour les clics sur la scène
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnMouseClicked(e -> {
                    restoreSelection();
                });
            }
        });
    }
    
    // Pour la compatibilité avec le code existant
    public BlockGridView() {
        this("default");
    }

    public void updateBlocks(List<Block> blocks) {
        this.allBlocks = new ArrayList<>(blocks); // Stocker une copie de tous les blocs
        
        // Afficher les blocs (filtrer si un texte de recherche est déjà présent)
        String searchText = searchField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            filterBlocks(searchText);
        } else {
            updateBlocksDisplay(blocks);
        }
    }
    
    // Méthode pour filtrer les blocs
    private void filterBlocks(String searchText) {
        if (allBlocks == null) return;
        
        if (searchText == null || searchText.trim().isEmpty()) {
            // Si pas de texte de recherche, afficher tous les blocs
            updateBlocksDisplay(allBlocks);
        } else {
            // Filtrer les blocs dont le nom contient le texte de recherche (insensible à la casse)
            String lowerCaseSearch = searchText.toLowerCase();
            List<Block> filteredBlocks = allBlocks.stream()
                .filter(block -> block.getName().toLowerCase().contains(lowerCaseSearch))
                .collect(Collectors.toList());
            
            updateBlocksDisplay(filteredBlocks);
        }
    }
    
    // Méthode pour afficher les blocs filtrés
    private void updateBlocksDisplay(List<Block> blocksToDisplay) {
        grid.getChildren().clear();
        
        // Ne nettoyer la sélection que si elle n'est plus valide
        VBox currentSelectedContainer = selectedContainers.get(gridId);
        if (currentSelectedContainer != null && !grid.getChildren().contains(currentSelectedContainer)) {
            selectedContainers.remove(gridId);
            selectedBlocks.remove(gridId);
        }

        int col = 0, row = 0;

        for (Block block : blocksToDisplay) {
            // Pour les blocs qui n'ont que des textures "side" sans texture "top",
            // nous avons besoin d'utiliser la texture disponible
            BufferedImage topTexture = block.getTexture(true);  // Essayer d'abord la texture top
            BufferedImage sideTexture = block.getTexture(false); // Texture side comme fallback
            
            // Utiliser la texture disponible (top en priorité, sinon side)
            BufferedImage textureToShow = (topTexture != null) ? topTexture : sideTexture;
            
            try {
                if (textureToShow != null) {
                    Image fxImage = convertToFXImage(textureToShow);
                    ImageView imageView = new ImageView(fxImage);
                    imageView.setFitWidth(48);
                    imageView.setFitHeight(48);
                    imageView.setSmooth(false);
                    imageView.setPreserveRatio(false);

                    Label nameLabel = new Label(block.getName());
                    nameLabel.getStyleClass().add("block-label");

                    VBox container = new VBox(5, imageView, nameLabel);
                    container.getStyleClass().add("block-container");
                    // Désactiver le style de focus par défaut
                    container.setFocusTraversable(false);

                    // Click = sélection
                    container.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1 && container != selectedContainers.get(gridId)) {
                            updateSelection(container);
                            selectedBlocks.put(gridId, block);
                        }
                    });

                    // Hover
                    container.setOnMouseEntered(e -> {
                        if (container != selectedContainers.get(gridId)) {
                            container.setBorder(new Border(new BorderStroke(
                                    Color.WHITE, BorderStrokeStyle.SOLID,
                                    new CornerRadii(3), new BorderWidths(1)
                            )));
                        }
                    });

                    container.setOnMouseExited(e -> {
                        if (container != selectedContainers.get(gridId)) {
                            container.setScaleX(1.0);
                            container.setScaleY(1.0);
                            container.setBorder(null);
                        }
                        // Restaurer la sélection pour éviter la perte du style
                        restoreSelection();
                    });

                    // Débogage du focus
                    container.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        System.out.println("Grid " + gridId + ": Container " + container + " focus changed to " + newVal);
                        if (newVal) {
                            restoreSelection();
                        }
                    });

                    grid.add(container, col, row);

                    if (++col >= 5) {
                        col = 0;
                        row++;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur image bloc " + block.getName() + " : " + e.getMessage());
            }
        }
        // Restaurer la sélection après la mise à jour des blocs
        restoreSelection();
    }

    private void updateSelection(VBox newContainer) {
        VBox currentSelectedContainer = selectedContainers.get(gridId);

        if (currentSelectedContainer != null && currentSelectedContainer != newContainer) {
            currentSelectedContainer.setScaleX(1.0);
            currentSelectedContainer.setScaleY(1.0);
            currentSelectedContainer.setBorder(null);
            currentSelectedContainer.setBackground(null);
            currentSelectedContainer.setCursor(Cursor.DEFAULT);
            currentSelectedContainer.setEffect(null);
            currentSelectedContainer.getStyleClass().removeIf(style -> style.equals("selected"));
            currentSelectedContainer.requestLayout();
        } else if (currentSelectedContainer == null) {
            System.out.println("Grid " + gridId + ": No previous selection");
        }

        selectedContainers.put(gridId, newContainer);

        // Appliquer le style pour les blocs sélectionnés
        newContainer.setScaleX(1.0);
        newContainer.setScaleY(1.0);
        newContainer.setBorder(new Border(new BorderStroke(
                Color.WHITE, BorderStrokeStyle.SOLID,
                new CornerRadii(3), new BorderWidths(2)
        )));
        newContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.1), CornerRadii.EMPTY, Insets.EMPTY
        )));
        newContainer.setCursor(Cursor.HAND);
        

        // Ajouter la classe selected seulement si elle n'est pas déjà présente
        if (!newContainer.getStyleClass().contains("selected")) {
            newContainer.getStyleClass().add("selected");
        }
        newContainer.requestLayout();
        if (newContainer.getParent() != null) {
            newContainer.getParent().requestLayout();
            newContainer.getParent().getScene().getRoot().requestLayout();
        }

        // Restaurer l'autre grille
        String otherGridId = gridId.equals("start") ? "end" : "start";
        BlockGridView otherGrid = gridInstances.get(otherGridId);
        if (otherGrid != null) {
            otherGrid.restoreSelection();
        }
    }

    public void restoreSelection() {
        VBox selectedContainer = selectedContainers.get(gridId);
        if (selectedContainer != null) {
            selectedContainer.setScaleX(1.0);
            selectedContainer.setScaleY(1.0);
            selectedContainer.setBorder(new Border(new BorderStroke(
                    Color.WHITE, BorderStrokeStyle.SOLID,
                    new CornerRadii(3), new BorderWidths(2)
            )));
            selectedContainer.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255, 255, 255, 0.1), CornerRadii.EMPTY, Insets.EMPTY
            )));
            selectedContainer.setCursor(Cursor.HAND);
            

            // Ajouter la classe selected seulement si elle n'est pas déjà présente
            if (!selectedContainer.getStyleClass().contains("selected")) {
                selectedContainer.getStyleClass().add("selected");
            }
            selectedContainer.requestLayout();
            if (selectedContainer.getParent() != null) {
                selectedContainer.getParent().requestLayout();
                selectedContainer.getParent().getScene().getRoot().requestLayout();
            }
        }
    }

    private Image convertToFXImage(BufferedImage image) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        return new Image(input, 48, 48, false, false);
    }

    public ScrollPane getRoot() {
        return root;
    }

    public Block getSelectedBlock() {
        return selectedBlocks.get(gridId);
    }

    public void setSelectedBlock(Block block) {
        selectedBlocks.put(gridId, block);
    }
    
    public String getGridId() {
        return gridId;
    }
    
    public void clearSelection() {
        System.out.println("Grid " + gridId + ": Clearing selection");
        VBox currentSelectedContainer = selectedContainers.get(gridId);
        if (currentSelectedContainer != null) {
            currentSelectedContainer.setScaleX(1.0);
            currentSelectedContainer.setScaleY(1.0);
            currentSelectedContainer.setBorder(null);
            currentSelectedContainer.setBackground(null);
            currentSelectedContainer.setCursor(Cursor.DEFAULT);
            currentSelectedContainer.setEffect(null);
            currentSelectedContainer.getStyleClass().removeIf(style -> style.equals("selected"));
            selectedContainers.remove(gridId);
            currentSelectedContainer.requestLayout();
        }
        selectedBlocks.remove(gridId);
    }
}