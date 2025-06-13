package skymc.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtils {
    public static Image resizeImage(Image image, int width, int height) {
        WritableImage resizedImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcX = (int) (x * image.getWidth() / width);
                int srcY = (int) (y * image.getHeight() / height);
                resizedImage.getPixelWriter().setColor(x, y, pixelReader.getColor(srcX, srcY));
            }
        }
        return resizedImage;
    }

    public static Color getAverageColor(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        double red = 0, green = 0, blue = 0;
        int pixelCount = (int) (image.getWidth() * image.getHeight());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
        }

        return Color.color(red / pixelCount, green / pixelCount, blue / pixelCount);
    }
}