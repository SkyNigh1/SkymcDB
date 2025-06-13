package skymc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import skymc.model.Block;

import java.util.List;

public class BilinearGradientView {
    private final BorderPane root;
    private final ComboBox<String> topLeftBlockComboBox;
    private final ComboBox<String> topRightBlockComboBox;
    private final ComboBox<String> bottomLeftBlockComboBox;
    private final ComboBox<String> bottomRightBlockComboBox;
    private final Spinner<Integer> gridSizeSpinner;
    private final Button generateButton;
    private final RadioButton useTopTextureRadio;
    private final RadioButton useSideTextureRadio;
    private final ToggleGroup textureToggleGroup;
    private final GridPane imageResultPane;
    private final FlowPane labelResultPane;
    private final Label resultLabel;
    private Label hoverInstructionLabel;

    public BilinearGradientView() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("2D Bilinear Gradient Generator");
        titleLabel.getStyleClass().add("title-label");

        VBox mainContainer = new VBox(10);
        mainContainer.getChildren().add(titleLabel);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.getStyleClass().add("LinearContainer");
        root.setCenter(mainContainer);

        // ComboBoxes for selecting corner blocks
        topLeftBlockComboBox = new ComboBox<>();
        topLeftBlockComboBox.getStyleClass().add("combo-box");
        topRightBlockComboBox = new ComboBox<>();
        topRightBlockComboBox.getStyleClass().add("combo-box");
        bottomLeftBlockComboBox = new ComboBox<>();
        bottomLeftBlockComboBox.getStyleClass().add("combo-box");
        bottomRightBlockComboBox = new ComboBox<>();
        bottomRightBlockComboBox.getStyleClass().add("combo-box");

        Label topLeftLabel = new Label("Top-Left Block:");
        topLeftLabel.getStyleClass().add("section-label");
        Label topRightLabel = new Label("Top-Right Block:");
        topRightLabel.getStyleClass().add("section-label");
        Label bottomLeftLabel = new Label("Bottom-Left Block:");
        bottomLeftLabel.getStyleClass().add("section-label");
        Label bottomRightLabel = new Label("Bottom-Right Block:");
        bottomRightLabel.getStyleClass().add("section-label");

        // Grid for ComboBoxes
        GridPane comboBoxGrid = new GridPane();
        comboBoxGrid.setHgap(10);
        comboBoxGrid.setVgap(10);
        comboBoxGrid.setAlignment(Pos.CENTER);
        comboBoxGrid.add(topLeftLabel, 0, 0);
        comboBoxGrid.add(topLeftBlockComboBox, 1, 0);
        comboBoxGrid.add(topRightLabel, 2, 0);
        comboBoxGrid.add(topRightBlockComboBox, 3, 0);
        comboBoxGrid.add(bottomLeftLabel, 0, 1);
        comboBoxGrid.add(bottomLeftBlockComboBox, 1, 1);
        comboBoxGrid.add(bottomRightLabel, 2, 1);
        comboBoxGrid.add(bottomRightBlockComboBox, 3, 1);

        mainContainer.getChildren().add(comboBoxGrid);

        // Grid size spinner
        gridSizeSpinner = new Spinner<>(3, 11, 5); // Grid size from 3x3 to 20x20, default 5x5
        gridSizeSpinner.getStyleClass().add("steps-spinner");
        gridSizeSpinner.setEditable(true);
        Label gridSizeLabel = new Label("Grid Size:");
        gridSizeLabel.getStyleClass().add("section-label");

        // Generate button
        generateButton = new Button("Generate 2D Gradient");
        generateButton.getStyleClass().add("generate-button");

        // Texture selection
        textureToggleGroup = new ToggleGroup();
        useTopTextureRadio = new RadioButton("Use Top Texture");
        useTopTextureRadio.getStyleClass().add("texture-radio");
        useSideTextureRadio = new RadioButton("Use Side Texture");
        useSideTextureRadio.getStyleClass().add("texture-radio");

        useTopTextureRadio.setToggleGroup(textureToggleGroup);
        useSideTextureRadio.setToggleGroup(textureToggleGroup);
        useTopTextureRadio.setSelected(true);

        Label textureOptionsLabel = new Label("Texture Options");
        textureOptionsLabel.setStyle("-fx-text-fill: white;");

        HBox controlsBox = new HBox(20, gridSizeLabel, gridSizeSpinner, generateButton, 
            new VBox(5, textureOptionsLabel, useTopTextureRadio, useSideTextureRadio));
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        mainContainer.getChildren().add(controlsBox);

        resultLabel = new Label("2D Gradient Result:");
        resultLabel.getStyleClass().add("section-label");

        imageResultPane = new GridPane();
        imageResultPane.getStyleClass().add("image-result-pane");
        imageResultPane.setHgap(0);
        imageResultPane.setVgap(0);
        imageResultPane.setAlignment(Pos.CENTER);

        labelResultPane = new FlowPane();
        labelResultPane.setHgap(5);
        labelResultPane.setVgap(5);
        labelResultPane.setPrefWrapLength(600);
        labelResultPane.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(10, resultLabel, imageResultPane, labelResultPane);
        resultBox.getStyleClass().add("label-result-pane");
        resultBox.setPadding(new Insets(10));
        mainContainer.getChildren().add(resultBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    public ComboBox<String> getTopLeftBlockComboBox() {
        return topLeftBlockComboBox;
    }

    public ComboBox<String> getTopRightBlockComboBox() {
        return topRightBlockComboBox;
    }

    public ComboBox<String> getBottomLeftBlockComboBox() {
        return bottomLeftBlockComboBox;
    }

    public ComboBox<String> getBottomRightBlockComboBox() {
        return bottomRightBlockComboBox;
    }

    public Spinner<Integer> getGridSizeSpinner() {
        return gridSizeSpinner;
    }

    public Button getGenerateButton() {
        return generateButton;
    }

    public boolean isUseTopTextureSelected() {
        return useTopTextureRadio.isSelected();
    }

    public GridPane getImageResultPane() {
        return imageResultPane;
    }

    public FlowPane getLabelResultPane() {
        return labelResultPane;
    }

    public void clearResults() {
        imageResultPane.getChildren().clear();
        labelResultPane.getChildren().clear();
    }

    public void setHoverInstructionLabel(Label label) {
        this.hoverInstructionLabel = label;
    }

    public Label getHoverInstructionLabel() {
        return this.hoverInstructionLabel;
    }

    public void updateBlockComboBoxes(List<Block> blocks) {
        String defaultValue = blocks.isEmpty() ? "" : blocks.get(0).getName();
        topLeftBlockComboBox.getSelectionModel().select(defaultValue);
        topRightBlockComboBox.getSelectionModel().select(defaultValue);
        bottomLeftBlockComboBox.getSelectionModel().select(defaultValue);
        bottomRightBlockComboBox.getSelectionModel().select(defaultValue);
    }
}