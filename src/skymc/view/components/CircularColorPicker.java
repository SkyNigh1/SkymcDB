package skymc.view.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 * A circular color picker component for selecting colors in HSB space.
 */
public class CircularColorPicker extends StackPane {
    private final Canvas canvas;
    private final Circle selector;
    private final ObjectProperty<Color> currentColor = new SimpleObjectProperty<>(Color.WHITE);
    private final double radius;
    private boolean dragging = false;
    private boolean updating = false;
    
    /**
     * Creates a new circular color picker with the specified diameter.
     * 
     * @param diameter The diameter of the color wheel in pixels
     */
    public CircularColorPicker(double diameter) {
        this.radius = diameter / 2;
        
        // Create canvas for the color wheel
        canvas = new Canvas(diameter, diameter);
        
        // Create selector circle
        selector = new Circle(5);
        selector.setStroke(Color.WHITE);
        selector.setStrokeWidth(2);
        selector.setFill(Color.TRANSPARENT);
        
        // Position selector at the edge of the wheel (full saturation, hue 0)
        updateSelectorPosition(0, 1.0);
        
        // Draw the color wheel
        drawColorWheel();
        
        // Add components
        getChildren().addAll(canvas, selector);
        
        // Setup event handlers
        setupEventHandlers();
    }
    
    /**
     * Draws the HSB color wheel on the canvas.
     */
    private void drawColorWheel() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw the hue circle as the outer ring
        for (int angle = 0; angle < 360; angle++) {
            Color hueColor = Color.hsb(angle, 1.0, 1.0);
            gc.setStroke(hueColor);
            
            double radians = Math.toRadians(angle);
            double startX = radius + (radius - 1) * Math.cos(radians);
            double startY = radius + (radius - 1) * Math.sin(radians);
            double endX = radius + radius * Math.cos(radians);
            double endY = radius + radius * Math.sin(radians);
            
            gc.strokeLine(startX, startY, endX, endY);
        }
        
        // Draw saturation as radial gradient from center (white) to edge (full color)
        for (int angle = 0; angle < 360; angle += 1) {
            Color hueColor = Color.hsb(angle, 1.0, 1.0);
            
            gc.setFill(new RadialGradient(
                0, 0,
                radius, radius,
                radius - 1,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(1, hueColor)
            ));
            
            double radians = Math.toRadians(angle);
            double nextRadians = Math.toRadians(angle + 1);
            
            double[] xPoints = {
                radius,
                radius + (radius - 1) * Math.cos(radians),
                radius + (radius - 1) * Math.cos(nextRadians)
            };
            
            double[] yPoints = {
                radius,
                radius + (radius - 1) * Math.sin(radians),
                radius + (radius - 1) * Math.sin(nextRadians)
            };
            
            gc.fillPolygon(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Sets up mouse event handlers for the color wheel.
     */
    private void setupEventHandlers() {
        canvas.setOnMousePressed(this::handleMouseEvent);
        canvas.setOnMouseDragged(this::handleMouseEvent);
        canvas.setOnMouseReleased(event -> dragging = false);
    }
    
    /**
     * Handles mouse events (press, drag) to update the selected color.
     */
    private void handleMouseEvent(MouseEvent event) {
        dragging = true;
        
        // Calculate position relative to center
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double x = event.getX() - centerX;
        double y = event.getY() - centerY;
        
        // Convert to polar coordinates
        double r = Math.sqrt(x * x + y * y);
        double theta = Math.toDegrees(Math.atan2(y, x));
        
        // Adjust angle to 0-360 range
        if (theta < 0) {
            theta += 360;
        }
        
        // Limit radius to the color wheel
        r = Math.min(r, radius);
        
        // Calculate hue and saturation
        double hue = theta;
        double saturation = r / radius;
        
        // Update the color and selector
        updateColor(hue, saturation, getCurrentColor().getBrightness());
        updateSelectorPosition(hue, saturation);
    }
    
    /**
     * Updates the position of the selector circle.
     */
    private void updateSelectorPosition(double hue, double saturation) {
        double radians = Math.toRadians(hue);
        double distance = saturation * radius;
        
        double x = radius + distance * Math.cos(radians);
        double y = radius + distance * Math.sin(radians);
        
        selector.setTranslateX(x - radius);
        selector.setTranslateY(y - radius);
    }
    
    /**
     * Updates the current color based on HSB values.
     */
    private void updateColor(double hue, double saturation, double brightness) {
        Color newColor = Color.hsb(hue, saturation, brightness);
        currentColor.set(newColor);
    }
    
    /**
     * Sets the current color and updates the selector position.
     */
    public void setCurrentColor(Color color) {
        if (updating || dragging) {
            return;
        }
        
        try {
            updating = true;
            currentColor.set(color);
            updateSelectorPosition(color.getHue(), color.getSaturation());
        } finally {
            updating = false;
        }
    }
    
    /**
     * Gets the current color property.
     */
    public ObjectProperty<Color> currentColorProperty() {
        return currentColor;
    }
    
    /**
     * Gets the current selected color.
     */
    public Color getCurrentColor() {
        return currentColor.get();
    }
}