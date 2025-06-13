package skymc.controller;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import skymc.model.ColorToBlockConverter;
import skymc.view.ColorToBlockView;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for the Color to Block converter functionality.
 * Handles the interaction between the view and the model.
 */
public class ColorToBlockController {
    private final ColorToBlockView view;
    private final ColorToBlockConverter model;
    
    /**
     * Creates a new controller instance.
     *
     * @param view The view to control
     * @param model The model to use for color conversion
     */
    public ColorToBlockController(ColorToBlockView view, ColorToBlockConverter model) {
        this.view = view;
        this.model = model;
        initialize();
    }
    
    /**
     * Initializes the controller by setting up event handlers.
     */
    private void initialize() {
        // Set up the find blocks button action
        view.getFindBlocksButton().setOnAction(e -> findBlocksForCurrentColor());
        
        // Update model when texture option changes
        view.getUseTopTextureRadio().setOnAction(e -> model.setUseTopTexture(true));
        view.getUseSideTextureRadio().setOnAction(e -> model.setUseTopTexture(false));
        
        // Update max results when spinner changes
        view.getResultsAmountSpinner().valueProperty().addListener((obs, oldVal, newVal) -> 
            model.setMaxResults(newVal));
        
        // Initialize model with view defaults
        model.setUseTopTexture(view.isUseTopTexture());
        model.setMaxResults(view.getResultsAmountSpinner().getValue());
    }
    
    /**
     * Finds blocks that match the currently selected color.
     */
    private void findBlocksForCurrentColor() {
        // Get the current color from the view
        Color fxColor = view.getCircularColorPicker().getCurrentColor();
        
        // Convert JavaFX color to AWT color for the model
        java.awt.Color awtColor = new java.awt.Color(
            (float) fxColor.getRed(),
            (float) fxColor.getGreen(),
            (float) fxColor.getBlue(),
            (float) fxColor.getOpacity()
        );
        
        // Update texture preference from the view
        model.setUseTopTexture(view.isUseTopTexture());
        
        // Update max results from the spinner
        model.setMaxResults(view.getResultsAmountSpinner().getValue());
        
        // Disable the button while processing
        view.getFindBlocksButton().setDisable(true);
        view.getFindBlocksButton().setText("Finding blocks...");
        
        // Run the block search asynchronously
        CompletableFuture.supplyAsync(() -> model.findClosestBlocks(awtColor))
            .thenAccept(results -> Platform.runLater(() -> {
                // Update the view with the results
                view.displayBlockResults(results);
                
                // Re-enable the button
                view.getFindBlocksButton().setDisable(false);
                view.getFindBlocksButton().setText("Find Blocks");
            }));
    }
}