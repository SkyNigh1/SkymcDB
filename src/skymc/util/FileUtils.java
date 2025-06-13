package skymc.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static File chooseSaveFile(Stage stage, String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        return fileChooser.showSaveDialog(stage);
    }

    public static void saveImage(Image image, File file) throws IOException {
        // Convert JavaFX Image to BufferedImage for saving
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color fxColor = image.getPixelReader().getColor(x, y);
                int argb = (int) (fxColor.getOpacity() * 255) << 24 |
                           (int) (fxColor.getRed() * 255) << 16 |
                           (int) (fxColor.getGreen() * 255) << 8 |
                           (int) (fxColor.getBlue() * 255);
                bufferedImage.setRGB(x, y, argb);
            }
        }
        ImageIO.write(bufferedImage, "png", file);
    }

    // Convert JavaFX Image to BufferedImage
    public static BufferedImage toBufferedImage(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = fxImage.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color fxColor = pixelReader.getColor(x, y);
                int argb = (int) (fxColor.getOpacity() * 255) << 24 |
                           (int) (fxColor.getRed() * 255) << 16 |
                           (int) (fxColor.getGreen() * 255) << 8 |
                           (int) (fxColor.getBlue() * 255);
                bufferedImage.setRGB(x, y, argb);
            }
        }
        return bufferedImage;
    }

    // Convert BufferedImage to JavaFX Image
    public static Image toFXImage(BufferedImage bImage) {
        WritableImage fxImage = new WritableImage(bImage.getWidth(), bImage.getHeight());
        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                int argb = bImage.getRGB(x, y);
                double red = ((argb >> 16) & 0xFF) / 255.0;
                double green = ((argb >> 8) & 0xFF) / 255.0;
                double blue = (argb & 0xFF) / 255.0;
                double alpha = ((argb >> 24) & 0xFF) / 255.0;
                fxImage.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.color(red, green, blue, alpha));
            }
        }
        return fxImage;
    }
}