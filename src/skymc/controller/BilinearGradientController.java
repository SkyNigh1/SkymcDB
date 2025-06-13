package skymc.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import skymc.model.Block;
import skymc.model.GradientGenerator;
import skymc.model.TextureManager;
import skymc.view.BilinearGradientView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.collections.FXCollections;

public class BilinearGradientController {
    private final BilinearGradientView view;
    private final GradientGenerator model;
    private List<Block> blockList;

    public BilinearGradientController(BilinearGradientView view, GradientGenerator model) {
        this.view = view;
        this.model = model;
        loadBlocks();
        setupEventHandlers();
    }

    private void loadBlocks() {
        try {
            TextureManager textureManager = TextureManager.getInstance();
            blockList = textureManager.getAllBlocks();
            List<String> blockNames = blockList.stream().map(Block::getName).sorted().toList();
            view.getTopLeftBlockComboBox().setItems(FXCollections.observableArrayList(blockNames));
            view.getTopRightBlockComboBox().setItems(FXCollections.observableArrayList(blockNames));
            view.getBottomLeftBlockComboBox().setItems(FXCollections.observableArrayList(blockNames));
            view.getBottomRightBlockComboBox().setItems(FXCollections.observableArrayList(blockNames));
            view.updateBlockComboBoxes(blockList);
        } catch (Exception e) {
            System.err.println("Error loading blocks: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Loading Blocks");
            alert.setHeaderText("Unable to load blocks");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupEventHandlers() {
        view.getGenerateButton().setOnAction(e -> generateGradient());
    }

    private void generateGradient() {
        String topLeftName = view.getTopLeftBlockComboBox().getValue();
        String topRightName = view.getTopRightBlockComboBox().getValue();
        String bottomLeftName = view.getBottomLeftBlockComboBox().getValue();
        String bottomRightName = view.getBottomRightBlockComboBox().getValue();

        Block topLeftBlock = blockList.stream().filter(b -> b.getName().equals(topLeftName)).findFirst().orElse(null);
        Block topRightBlock = blockList.stream().filter(b -> b.getName().equals(topRightName)).findFirst().orElse(null);
        Block bottomLeftBlock = blockList.stream().filter(b -> b.getName().equals(bottomLeftName)).findFirst().orElse(null);
        Block bottomRightBlock = blockList.stream().filter(b -> b.getName().equals(bottomRightName)).findFirst().orElse(null);

        if (topLeftBlock == null || topRightBlock == null || bottomLeftBlock == null || bottomRightBlock == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Incomplete Selection");
            alert.setHeaderText("Please select all four corner blocks");
            alert.showAndWait();
            return;
        }

        int gridSize = view.getGridSizeSpinner().getValue();
        boolean useTopTexture = view.isUseTopTextureSelected();

        try {
            Block[][] gradient = model.generateBilinearGradient(
                topLeftBlock, topRightBlock, bottomLeftBlock, bottomRightBlock, gridSize, useTopTexture);
            displayGradient(gradient);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to generate 2D gradient");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void displayGradient(Block[][] gradient) {
        view.clearResults();

        Label hoverInstructionLabel = new Label("Hover over a texture to see its name");
        hoverInstructionLabel.getStyleClass().add("hover-gradient");
        view.getLabelResultPane().getChildren().add(hoverInstructionLabel);
        view.setHoverInstructionLabel(hoverInstructionLabel);

        for (int row = 0; row < gradient.length; row++) {
            for (int col = 0; col < gradient[row].length; col++) {
                Block block = gradient[row][col];
                try {
                    BufferedImage texture = block.getTexture(view.isUseTopTextureSelected());
                    if (texture != null) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(texture, "png", outputStream);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                        Image fxImage = new Image(inputStream, 48, 48, false, false);

                        ImageView imageView = new ImageView(fxImage);
                        imageView.setFitWidth(48);
                        imageView.setFitHeight(48);
                        imageView.setSmooth(false);
                        imageView.setPreserveRatio(false);
                        imageView.getStyleClass().add("pixel-perfect");

                        final Block currentBlock = block;
                        imageView.setOnMouseEntered(e -> {
                            hoverInstructionLabel.setText(currentBlock.getName());
                            imageView.setScaleX(1.1);
                            imageView.setScaleY(1.1);
                        });

                        imageView.setOnMouseExited(e -> {
                            hoverInstructionLabel.setText("Hover over a texture to see its name");
                            imageView.setScaleX(1.0);
                            imageView.setScaleY(1.0);
                        });

                        view.getImageResultPane().add(imageView, col, row);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to display block " + block.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}