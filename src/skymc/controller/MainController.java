package skymc.controller;

import org.fusesource.jansi.Ansi;
import javafx.stage.Stage;
import skymc.model.*;
import skymc.view.*;

public class MainController {
    private MainView view;
    private Stage stage;

    public MainController(MainView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        initializeControllers();
    }

    private void initializeControllers() {
        System.out.println("Initializing controllers...");

        // Linear Gradient
        LinearGradientView linearGradientView = new LinearGradientView();
        new LinearGradientController(linearGradientView, new GradientGenerator());
        view.addTab("Linear Gradient", linearGradientView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added Linear Gradient tab").reset());

        // 2D Bilinear Gradient
        BilinearGradientView bilinearGradientView = new BilinearGradientView();
        new BilinearGradientController(bilinearGradientView, new GradientGenerator());
        view.addTab("2D Bilinear Gradient", bilinearGradientView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added 2D Bilinear Gradient tab").reset());

        // Color to Block Converter
        ColorToBlockView colorToBlockView = new ColorToBlockView();
        new ColorToBlockController(colorToBlockView, new ColorToBlockConverter());
        view.addTab("Color to Block", colorToBlockView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added Color to Block tab").reset());

        // Pixel Art Converter
        PixelArtView pixelArtView = new PixelArtView();
        new PixelArtController(pixelArtView, new PixelArtConverter(), stage);
        view.addTab("Image to Pixel-Art", pixelArtView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added Image to Pixel-Art tab").reset());

        // Palette Viewer
        PaletteViewerView paletteViewerView = new PaletteViewerView();
        view.addTab("Palette Viewer", paletteViewerView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added Palette Viewer tab").reset());

        // Pattern Generator
        PatternView patternGeneratorView = new PatternView();
        new PatternController(patternGeneratorView, new PatternGenerator());
        view.addTab("Pattern Generator", patternGeneratorView);
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added Pattern Generator tab").reset());

        // About Tab
        AboutView aboutView = new AboutView();
        view.addTab("About", aboutView.getRoot());
        System.out.println(Ansi.ansi().fgBrightBlue().a("Added About tab").reset());
    }
}
