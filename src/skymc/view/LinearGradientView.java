package skymc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import skymc.view.components.BlockGridView;

public class LinearGradientView {
    private final BorderPane root;
    private final BlockGridView startBlockGrid;
    private final BlockGridView endBlockGrid;
    private final Spinner<Integer> stepsSpinner;
    private final Button generateButton;
    private final RadioButton useTopTextureRadio;
    private final RadioButton useSideTextureRadio;
    private final ToggleGroup textureToggleGroup;
    private final GridPane imageResultPane;
    private final FlowPane labelResultPane;
    private final Label resultLabel;
    private Label hoverInstructionLabel;

    public LinearGradientView() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Linear Gradient Generator");
        titleLabel.getStyleClass().add("title-label");

        VBox mainContainer = new VBox(10);
        mainContainer.getChildren().add(titleLabel);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.getStyleClass().add("LinearContainer");
        root.setCenter(mainContainer);

        startBlockGrid = new BlockGridView("start");
        endBlockGrid = new BlockGridView("end");

        Label startLabel = new Label("Bloc de départ:");
        startLabel.getStyleClass().add("section-label");

        Label endLabel = new Label("Bloc d'arrivée:");
        endLabel.getStyleClass().add("section-label");

        VBox startBox = new VBox(5, startLabel, startBlockGrid.getRoot());
        startBox.getStyleClass().add("start-box");
        VBox endBox = new VBox(5, endLabel, endBlockGrid.getRoot());
        endBox.getStyleClass().add("start-box");

        HBox gridsBox = new HBox(20, startBox, endBox);
        gridsBox.setAlignment(Pos.CENTER);
        mainContainer.getChildren().add(gridsBox);

        stepsSpinner = new Spinner<>(1, 20, 10);
        stepsSpinner.getStyleClass().add("steps-spinner");
        stepsSpinner.setEditable(true);
        Label stepsLabel = new Label("Nombre d'étapes:");
        stepsLabel.getStyleClass().add("section-label");

        generateButton = new Button("Générer le dégradé");
        generateButton.getStyleClass().add("generate-button");

        textureToggleGroup = new ToggleGroup();
        useTopTextureRadio = new RadioButton("Utiliser la texture de dessus");
        useTopTextureRadio.getStyleClass().add("texture-radio");
        useSideTextureRadio = new RadioButton("Utiliser la texture latérale");
        useSideTextureRadio.getStyleClass().add("texture-radio");

        useTopTextureRadio.setToggleGroup(textureToggleGroup);
        useSideTextureRadio.setToggleGroup(textureToggleGroup);
        useTopTextureRadio.setSelected(true);

        HBox stepsBox = new HBox(10, stepsLabel, stepsSpinner);
        VBox optionsBox = new VBox(5, new Label("Options de texture"), useTopTextureRadio, useSideTextureRadio);
        optionsBox.getStyleClass().add("options-box");
        HBox controlsBox = new HBox(20, stepsBox, generateButton, optionsBox);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        mainContainer.getChildren().add(controlsBox);

        resultLabel = new Label("Résultat du dégradé:");
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

        VBox resultBox = new VBox(10);
        resultBox.getStyleClass().add("label-result-pane");
        resultBox.getChildren().addAll(resultLabel, imageResultPane, labelResultPane);
        resultBox.setPadding(new Insets(10));

        mainContainer.getChildren().add(resultBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    public BlockGridView getStartBlockGrid() {
        return startBlockGrid;
    }

    public BlockGridView getEndBlockGrid() {
        return endBlockGrid;
    }

    public Spinner<Integer> getStepsSpinner() {
        return stepsSpinner;
    }

    public Button getGenerateButton() {
        return generateButton;
    }

    public boolean isUseTopTextureSelected() {
        return useTopTextureRadio.isSelected();
    }

    public ToggleGroup getTextureToggleGroup() {
        return textureToggleGroup;
    }

    public GridPane getImageResultPane() {
        return imageResultPane;
    }

    public FlowPane getLabelResultPane() {
        return labelResultPane;
    }

    public FlowPane getResultPane() {
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

    /**
     * Crée un conteneur VBox avec une ImageView et un label, et applique l'effet de survol.
     */
    public VBox createImageWithHoverLabel(ImageView imageView, String labelText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("hover-gradient");

        VBox container = new VBox(5, imageView, label);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-padding: 5px;");

        imageView.setOnMouseEntered(e -> label.setStyle("-fx-text-fill: #00ff62;"));
        imageView.setOnMouseExited(e -> label.setStyle("-fx-text-fill: white;"));

        return container;
    }
}