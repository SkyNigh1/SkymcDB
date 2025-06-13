package skymc.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import skymc.model.Block;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 * Component representing a single block result item in the color to block converter.
 */
public class BlockResultItem extends HBox {
    private final Block block;
    private final double matchPercentage;
    private final boolean useTopTexture;
    
    /**
     * Creates a new block result item.
     *
     * @param block The Minecraft block
     * @param matchPercentage The match percentage (0-100)
     */
    public BlockResultItem(Block block, double matchPercentage) {
        this(block, matchPercentage, true);
    }
    
    /**
     * Creates a new block result item with texture preference.
     *
     * @param block The Minecraft block
     * @param matchPercentage The match percentage (0-100)
     * @param useTopTexture Whether to use top texture (true) or side texture (false)
     */
    public BlockResultItem(Block block, double matchPercentage, boolean useTopTexture) {
        this.block = block;
        this.matchPercentage = matchPercentage;
        this.useTopTexture = useTopTexture;
        
        setPadding(new Insets(10));
        setSpacing(15);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("label-result-pane");
        
        // Create block display with image and color
        createContent();
    }
    
    /**
     * Creates the content of the block result item.
     */
    private void createContent() {
        // Block texture image - try to get the texture based on preference
        BufferedImage texture = block.getTexture(useTopTexture);
        
        // If no texture found with the preferred side, try the other side
        if (texture == null) {
            texture = block.getTexture(!useTopTexture);
        }
        
        ImageView blockImage;
        
        if (texture != null) {
            Image fxImage = convertToFXImage(texture);
            blockImage = new ImageView(fxImage != null ? fxImage : createPlaceholderImage());
        } else {
            blockImage = new ImageView(createPlaceholderImage());
        }
        
        // Set exact dimensions to match 16x16 texture size when scaled properly
        blockImage.setFitHeight(48);
        blockImage.setFitWidth(48);
        
        // These properties are crucial for pixelated rendering
        blockImage.setSmooth(false);
        blockImage.setPreserveRatio(true);
        
        // Block info section
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        // Block name
        Label nameLabel = new Label(block.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        
        // Match percentage
        HBox matchBox = new HBox(10);
        matchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label percentLabel = new Label(String.format("%.1f%%", matchPercentage));
        percentLabel.setStyle("-fx-text-fill: white;");
        
        ProgressBar progressBar = new ProgressBar(matchPercentage / 100.0);
        progressBar.setPrefWidth(120);
        // Style based on match percentage
        if (matchPercentage > 80) {
            progressBar.setStyle("-fx-accent: #00ff00;"); // Green for good match
        } else if (matchPercentage > 50) {
            progressBar.setStyle("-fx-accent: #ffff00;"); // Yellow for medium match
        } else {
            progressBar.setStyle("-fx-accent: #ff6600;"); // Orange for poor match
        }
        
        matchBox.getChildren().addAll(percentLabel, progressBar);
        
        infoBox.getChildren().addAll(nameLabel, matchBox);
        
        // Block average color preview
        Rectangle colorRect = new Rectangle(48, 48);
        
        // Get average color and handle null case
        // Try to get color based on the texture that was actually used
        java.awt.Color avgAwtColor = null;
        
        // First try with preferred texture side
        avgAwtColor = block.getAverageColor(useTopTexture);
        
        // If null, try the other side
        if (avgAwtColor == null) {
            avgAwtColor = block.getAverageColor(!useTopTexture);
        }
        
        Color avgColor = avgAwtColor != null ? 
                         convertToFXColor(avgAwtColor) : 
                         Color.GRAY; // Default color if null
        
        colorRect.setFill(avgColor);
        colorRect.setStroke(Color.WHITE);
        colorRect.setStrokeWidth(1);
        colorRect.setArcWidth(4);
        colorRect.setArcHeight(4);
        
        // Add all components to the container
        getChildren().addAll(blockImage, infoBox, colorRect);
    }
    
    /**
     * Creates a simple placeholder image when texture is unavailable.
     * @return A placeholder image
     */
    private Image createPlaceholderImage() {
        // Create a checkerboard pattern to indicate missing texture
        int width = 16, height = 16;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Draw checkerboard pattern with magenta/black (classic missing texture)
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(java.awt.Color.MAGENTA);
        
        int squareSize = 8;
        for (int x = 0; x < width; x += squareSize) {
            for (int y = 0; y < height; y += squareSize) {
                if ((x / squareSize + y / squareSize) % 2 == 0) {
                    g.fillRect(x, y, squareSize, squareSize);
                }
            }
        }
        g.dispose();
        
        return convertToFXImage(img);
    }
    
    /**
     * Converts AWT Color to JavaFX Color.
     *
     * @param awtColor AWT Color object
     * @return JavaFX Color object
     */
    private Color convertToFXColor(java.awt.Color awtColor) {
        if (awtColor == null) {
            return Color.GRAY; // Default color if null
        }
        
        return Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
    }
    
    /**
     * Converts AWT BufferedImage to JavaFX Image.
     *
     * @param bufferedImage AWT BufferedImage
     * @return JavaFX Image object or null if conversion fails
     */
    private Image convertToFXImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            
            // CRITICAL FIX: Changed parameters to match your working code
            // Old: new Image(in, 0, 0, true, false);
            // New: Create the image with fixed size and disable smooth
            return new Image(in, 48, 48, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets the block associated with this result item.
     */
    public Block getBlock() {
        return block;
    }
    
    /**
     * Gets the match percentage for this result.
     */
    public double getMatchPercentage() {
        return matchPercentage;
    }
}