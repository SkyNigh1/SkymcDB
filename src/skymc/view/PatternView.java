package skymc.view;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import skymc.model.Block;
import skymc.model.PatternGenerator;

public class PatternView extends BorderPane {
    private ComboBox<PatternGenerator.PatternType> patternTypeComboBox;
    private ComboBox<PatternGenerator.CurveType> curveTypeComboBox;
    private ComboBox<Block> startBlockComboBox;
    private ComboBox<Block> endBlockComboBox;
    private Slider sizeSlider;
    private Slider noiseSlider;
    private Button generateButton;
    private GridPane patternGrid;
    private RadioButton useTopTextureRadio;
    private RadioButton useSideTextureRadio;
    private ToggleGroup textureToggleGroup;

    public PatternView() {
        initializeUI();
    }

    private void initializeUI() {
        // Pattern Type ComboBox
        patternTypeComboBox = new ComboBox<>();
        patternTypeComboBox.getItems().addAll(PatternGenerator.PatternType.values());
        patternTypeComboBox.setValue(PatternGenerator.PatternType.LINEAR_HORIZONTAL);
        patternTypeComboBox.getStyleClass().add("combo-box");

        // Curve Type ComboBox
        curveTypeComboBox = new ComboBox<>();
        curveTypeComboBox.getItems().addAll(PatternGenerator.CurveType.values());
        curveTypeComboBox.setValue(PatternGenerator.CurveType.LINEAR);
        curveTypeComboBox.getStyleClass().add("combo-box");

        // Start Block ComboBox
        startBlockComboBox = new ComboBox<>();
        startBlockComboBox.getStyleClass().add("combo-box");

        // End Block ComboBox
        endBlockComboBox = new ComboBox<>();
        endBlockComboBox.getStyleClass().add("combo-box");

        // Size Slider
        sizeSlider = new Slider(1, 50, 10);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(10);
        sizeSlider.setMinorTickCount(1);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.getStyleClass().add("steps-spinner");

        // Noise Slider
        noiseSlider = new Slider(0, 1, 0);
        noiseSlider.setShowTickLabels(true);
        noiseSlider.setShowTickMarks(true);
        noiseSlider.setMajorTickUnit(0.2);
        noiseSlider.setMinorTickCount(1);
        noiseSlider.setSnapToTicks(true);
        noiseSlider.getStyleClass().add("steps-spinner");

        // Generate Button
        generateButton = new Button("Generate Pattern");
        generateButton.getStyleClass().add("generate-button");

        // Texture Toggle Group
        textureToggleGroup = new ToggleGroup();
        useTopTextureRadio = new RadioButton("Use Top Texture");
        useTopTextureRadio.getStyleClass().add("texture-radio");
        useSideTextureRadio = new RadioButton("Use Side Texture");
        useSideTextureRadio.getStyleClass().add("texture-radio");

        useTopTextureRadio.setToggleGroup(textureToggleGroup);
        useSideTextureRadio.setToggleGroup(textureToggleGroup);
        useTopTextureRadio.setSelected(true);

        // Pattern Grid
        patternGrid = new GridPane();
        patternGrid.getStyleClass().add("image-result-pane");

        // Layout for controls
        VBox controls = new VBox(10);
        controls.getStyleClass().add("start-box");

        // Labels
        Label patternTypeLabel = new Label("Pattern Type:");
        patternTypeLabel.getStyleClass().add("section-label");

        Label curveTypeLabel = new Label("Curve Type:");
        curveTypeLabel.getStyleClass().add("section-label");

        Label startBlockLabel = new Label("Start Block:");
        startBlockLabel.getStyleClass().add("section-label");

        Label endBlockLabel = new Label("End Block:");
        endBlockLabel.getStyleClass().add("section-label");

        Label sizeLabel = new Label("Size:");
        sizeLabel.getStyleClass().add("section-label");

        Label noiseLabel = new Label("Noise Level:");
        noiseLabel.getStyleClass().add("section-label");

        Label textureOptionsLabel = new Label("Texture Options:");
        textureOptionsLabel.getStyleClass().add("section-label");

        controls.getChildren().addAll(
            patternTypeLabel, patternTypeComboBox,
            curveTypeLabel, curveTypeComboBox,
            startBlockLabel, startBlockComboBox,
            endBlockLabel, endBlockComboBox,
            sizeLabel, sizeSlider,
            noiseLabel, noiseSlider,
            textureOptionsLabel, useTopTextureRadio, useSideTextureRadio,
            generateButton
        );

        // Set controls on the left side
        this.setLeft(controls);

        // Set pattern grid on the right side
        ScrollPane scrollPane = new ScrollPane(patternGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        this.setCenter(scrollPane);
    }

    public ComboBox<PatternGenerator.PatternType> getPatternTypeComboBox() {
        return patternTypeComboBox;
    }

    public ComboBox<PatternGenerator.CurveType> getCurveTypeComboBox() {
        return curveTypeComboBox;
    }

    public ComboBox<Block> getStartBlockComboBox() {
        return startBlockComboBox;
    }

    public ComboBox<Block> getEndBlockComboBox() {
        return endBlockComboBox;
    }

    public Slider getSizeSlider() {
        return sizeSlider;
    }

    public Slider getNoiseSlider() {
        return noiseSlider;
    }

    public Button getGenerateButton() {
        return generateButton;
    }

    public GridPane getPatternGrid() {
        return patternGrid;
    }

    public boolean isUseTopTextureSelected() {
        return useTopTextureRadio.isSelected();
    }

    public void displayPattern(Block[][] pattern) {
        patternGrid.getChildren().clear();
        for (int y = 0; y < pattern.length; y++) {
            for (int x = 0; x < pattern[y].length; x++) {
                Block block = pattern[y][x];
                if (block != null) {
                    String texturePath = isUseTopTextureSelected() ? block.getTopTexturePath() : block.getSideTexturePath();
                    if (texturePath != null) {
                        ImageView blockImageView = new ImageView(new Image("file:" + texturePath));
                        blockImageView.setFitWidth(20);
                        blockImageView.setFitHeight(20);
                        blockImageView.getStyleClass().add("pixel-perfect");
                        patternGrid.add(blockImageView, x, y);
                    }
                }
            }
        }
    }
}
