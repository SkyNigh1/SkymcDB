package skymc;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fusesource.jansi.Ansi;
import skymc.controller.MainController;
import skymc.view.MainView;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println(Ansi.ansi().fgBrightGreen().a("Starting application...").reset());
        System.out.println("Creating MainView...");
        MainView view = new MainView();
        System.out.println("MainView created.");
        System.out.println("Creating MainController...");
        new MainController(view, primaryStage);
        System.out.println("MainController created.");
        primaryStage.setTitle("SkymcDB v1.4 - Minecraft Builder Tools");
        System.out.println("Setting up scene...");
        var scene = new javafx.scene.Scene(view.getRoot(), 800, 600);
        scene.getStylesheets().add("skymc/style.css");
        System.out.println("Scene set up, showing stage...");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        System.out.println(Ansi.ansi().fgBrightGreen().a("Stage shown.").reset());
    }

    public static void main(String[] args) {
        System.out.println("Main method called.");
        launch(args);
    }
    
}