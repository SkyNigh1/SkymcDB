package skymc.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import skymc.model.Block;
import skymc.model.ColorToBlockConverter;
import skymc.view.components.CircularColorPicker;
import skymc.view.components.BlockResultItem;

import java.util.List;

/**
 * Vue pour la conversion de couleurs en blocs Minecraft.
 * Fond uniforme en rgba(20, 20, 20, 0.4)
 */
public class ColorToBlockView {
    private BorderPane root;
    private GridPane resultsGrid;
    private CircularColorPicker circularColorPicker;
    private Slider hueSlider;
    private Slider saturationSlider;
    private Slider brightnessSlider;
    private TextField hexCodeField;
    private Spinner<Integer> resultsAmountSpinner;
    private RadioButton useTopTextureRadio;
    private RadioButton useSideTextureRadio;
    private Button findBlocksButton;
    private ScrollPane resultsScrollPane;
    private VBox resultsContainer;
    private Rectangle colorPreview;
    
    // Properties to track HSB values
    private final SimpleDoubleProperty hueProperty = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty saturationProperty = new SimpleDoubleProperty(100);
    private final SimpleDoubleProperty brightnessProperty = new SimpleDoubleProperty(100);

    public ColorToBlockView() {
        // Initialize the root layout with rgba(20, 20, 20, 0.4) background
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: rgba(30, 30, 30, 0.1);");

        // Title at the top
        Label titleLabel = new Label("Color to Block Converter");
        titleLabel.getStyleClass().add("app-title");
        root.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // Left panel: Color selection controls
        VBox leftPanel = createLeftPanel();
        leftPanel.getStyleClass().add("start-box");
        root.setLeft(leftPanel);

        // Create results area on the right
        resultsContainer = new VBox(10);
        resultsContainer.getStyleClass().add("vbox");
        
        resultsGrid = new GridPane();
        resultsGrid.setHgap(10);
        resultsGrid.setVgap(10);
        resultsContainer.getChildren().add(resultsGrid);
        
        resultsScrollPane = new ScrollPane(resultsContainer);
        resultsScrollPane.setFitToWidth(true);
        resultsScrollPane.getStyleClass().add("scroll-pane");
        resultsScrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(resultsScrollPane);
        BorderPane.setMargin(resultsScrollPane, new Insets(0, 0, 0, 20));

        // Set up bindings and listeners
        setupBindingsAndListeners();
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: rgba(20, 20, 20, 0.4); -fx-border-color: rgba(255, 255, 255); -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        panel.setPadding(new Insets(15));

        // Create circular color picker (150px diameter)
        circularColorPicker = new CircularColorPicker(150);
        
        // Create color preview rectangle
        colorPreview = new Rectangle(100, 50);
        colorPreview.getStyleClass().add("color-preview");
        colorPreview.setFill(Color.WHITE);
        colorPreview.setStroke(Color.WHITE);
        colorPreview.setStrokeWidth(1);
        colorPreview.setArcWidth(8);
        colorPreview.setArcHeight(8);
        
        // HBox to contain color picker and color preview
        HBox colorPickerBox = new HBox(20);
        colorPickerBox.setAlignment(Pos.CENTER);
        colorPickerBox.getChildren().addAll(circularColorPicker, colorPreview);

        // Hex color input
        hexCodeField = new TextField("#FFFFFF");
        hexCodeField.setPrefWidth(120);
        hexCodeField.setPromptText("Hex Color");
        hexCodeField.getStyleClass().add("block-search-field");

        // HSB sliders
        hueSlider = createSlider("Hue Tint", 0, 360, 0);
        saturationSlider = createSlider("Saturation", 0, 100, 100);
        brightnessSlider = createSlider("Brightness", 0, 100, 100);

        // Results spinner
        resultsAmountSpinner = new Spinner<>(1, 50, 10);
        resultsAmountSpinner.setEditable(true);
        resultsAmountSpinner.setPrefWidth(100);
        resultsAmountSpinner.getStyleClass().add("steps-spinner");
        
        HBox spinnerBox = new HBox(10);
        spinnerBox.setAlignment(Pos.CENTER_LEFT);
        Label amountLabel = new Label("Results Amount:");
        amountLabel.setStyle("-fx-text-fill: white;");
        spinnerBox.getChildren().addAll(amountLabel, resultsAmountSpinner);

        // Texture selection options
        Label optionsLabel = new Label("Texture Options");
        optionsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        optionsLabel.setTextFill(Color.WHITE);

        ToggleGroup textureGroup = new ToggleGroup();
        useTopTextureRadio = new RadioButton("Use the top texture");
        useTopTextureRadio.setSelected(true);
        useTopTextureRadio.setToggleGroup(textureGroup);
        useTopTextureRadio.getStyleClass().add("texture-radio");

        useSideTextureRadio = new RadioButton("Use the side texture");
        useSideTextureRadio.setToggleGroup(textureGroup);
        useSideTextureRadio.getStyleClass().add("texture-radio");

        // Find blocks button
        findBlocksButton = new Button("Find Blocks");
        findBlocksButton.getStyleClass().add("generate-button");
        findBlocksButton.setMaxWidth(Double.MAX_VALUE);
        
        // Add all components to the panel
        panel.getChildren().addAll(
            colorPickerBox,
            hexCodeField,
            hueSlider.getParent(),
            saturationSlider.getParent(),
            brightnessSlider.getParent(),
            spinnerBox,
            optionsLabel,
            useTopTextureRadio,
            useSideTextureRadio,
            findBlocksButton
        );
        
        return panel;
    }
    
    private Slider createSlider(String name, double min, double max, double initial) {
        Slider slider = new Slider(min, max, initial);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(3);
        slider.setPrefWidth(280);
        
        Label label = new Label(name);
        label.setTextFill(Color.WHITE);
        
        VBox box = new VBox(5);
        box.getChildren().addAll(label, slider);
        
        return slider;
    }
    
    // Flag to prevent infinite property update cycles
    private boolean updatingFromSliders = false;
    private boolean updatingFromColorPicker = false;
    
    private void setupBindingsAndListeners() {
        // Bind sliders to properties
        hueSlider.valueProperty().bindBidirectional(hueProperty);
        saturationSlider.valueProperty().bindBidirectional(saturationProperty);
        brightnessSlider.valueProperty().bindBidirectional(brightnessProperty);
        
        // Update color preview when sliders change
        ChangeListener<Number> hsbChangeListener = (obs, oldVal, newVal) -> {
            if (!updatingFromColorPicker) {
                updatingFromSliders = true;
                try {
                    Color color = Color.hsb(
                        hueProperty.get(),
                        saturationProperty.get() / 100.0,
                        brightnessProperty.get() / 100.0
                    );
                    colorPreview.setFill(color);
                    circularColorPicker.setCurrentColor(color);
                    updateHexField(color);
                } finally {
                    updatingFromSliders = false;
                }
            }
        };
        
        hueProperty.addListener(hsbChangeListener);
        saturationProperty.addListener(hsbChangeListener);
        brightnessProperty.addListener(hsbChangeListener);
        
        // Update from circular picker
        circularColorPicker.currentColorProperty().addListener((obs, oldColor, newColor) -> {
            if (newColor != null && !updatingFromSliders) {
                updatingFromColorPicker = true;
                try {
                    // Update the sliders without triggering their listeners
                    hueProperty.set(newColor.getHue());
                    saturationProperty.set(newColor.getSaturation() * 100);
                    brightnessProperty.set(newColor.getBrightness() * 100);
                    
                    colorPreview.setFill(newColor);
                    updateHexField(newColor);
                } finally {
                    updatingFromColorPicker = false;
                }
            }
        });
        
        // Handle hex code updates
        hexCodeField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    Color color = Color.web(hexCodeField.getText());
                    circularColorPicker.setCurrentColor(color);
                    colorPreview.setFill(color);
                    
                    hueProperty.set(color.getHue());
                    saturationProperty.set(color.getSaturation() * 100);
                    brightnessProperty.set(color.getBrightness() * 100);
                } catch (Exception e) {
                    // Invalid hex code
                    hexCodeField.setStyle("-fx-border-color: red;");
                }
            }
        });
        
        hexCodeField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    Color color = Color.web(hexCodeField.getText());
                    circularColorPicker.setCurrentColor(color);
                    colorPreview.setFill(color);
                    
                    hueProperty.set(color.getHue());
                    saturationProperty.set(color.getSaturation() * 100);
                    brightnessProperty.set(color.getBrightness() * 100);
                    
                    hexCodeField.setStyle("");
                } catch (Exception e) {
                    // Invalid hex code
                    hexCodeField.setStyle("-fx-border-color: red;");
                }
            }
        });
    }
    
    private void updateHexField(Color color) {
        String hex = String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
        hexCodeField.setText(hex);
    }
    
    public void displayBlockResults(List<ColorToBlockConverter.BlockDistanceResult> results) {
        resultsContainer.getChildren().clear();
        
        Label resultsLabel = new Label("Blocks matching your color:");
        resultsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        resultsLabel.setTextFill(Color.WHITE);
        resultsContainer.getChildren().add(resultsLabel);
        
        VBox blocksContainer = new VBox(10);
        blocksContainer.setStyle("-fx-background-color: rgba(20, 20, 20, 0.4);");
        
        boolean useTopTexture = useTopTextureRadio.isSelected();
        
        for (ColorToBlockConverter.BlockDistanceResult result : results) {
            Block block = result.getBlock();
            double distance = result.getDistance();
            
            // Calculate match percentage (closer to 0 distance means better match)
            // Map from distance (0-442) to percentage (100-0)
            double maxDistance = 442; // Max possible distance for RGB (sqrt(255^2 + 255^2 + 255^2))
            double matchPercentage = Math.max(0, Math.min(100, 100 * (1 - distance / maxDistance)));
            
            // Pass the texture preference to the BlockResultItem
            BlockResultItem resultItem = new BlockResultItem(block, matchPercentage, useTopTexture);
            blocksContainer.getChildren().add(resultItem);
        }
        
        resultsContainer.getChildren().add(blocksContainer);
    }

    public BorderPane getRoot() {
        return root;
    }

    public CircularColorPicker getCircularColorPicker() {
        return circularColorPicker;
    }

    public Slider getHueSlider() {
        return hueSlider;
    }

    public Slider getSaturationSlider() {
        return saturationSlider;
    }

    public Slider getBrightnessSlider() {
        return brightnessSlider;
    }

    public TextField getHexCodeField() {
        return hexCodeField;
    }

    public Spinner<Integer> getResultsAmountSpinner() {
        return resultsAmountSpinner;
    }

    public boolean isUseTopTexture() {
        return useTopTextureRadio.isSelected();
    }

    public RadioButton getUseTopTextureRadio() {
        return useTopTextureRadio;
    }

    public RadioButton getUseSideTextureRadio() {
        return useSideTextureRadio;
    }

    public Button getFindBlocksButton() {
        return findBlocksButton;
    }
}