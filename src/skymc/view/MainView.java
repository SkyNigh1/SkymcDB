package skymc.view;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.fusesource.jansi.Ansi;

import javafx.application.Platform;
import javafx.scene.layout.Region;

/**
 * Vue principale de l'application SkymcDB qui organise toutes les 
 * fonctionnalités dans un système d'onglets.
 */
public class MainView {
    private final BorderPane root;
    private final TabPane tabPane;
    
    public MainView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        
        Button closeButton = new Button("Exit");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> {
            System.out.println(Ansi.ansi().fgBrightRed().a("\n Application closed.").reset());
            Platform.exit();
        });
        
        Label titleLabel = new Label("SkyMC Builder Tools");
        titleLabel.getStyleClass().add("app-title");
        
        // Création du logo
        ImageView logoView = null;
        try {
            Image logoImage = new Image("file:assets/images/logo.png");
            logoView = new ImageView(logoImage);
            logoView.setFitHeight(40); // Ajustez la taille selon vos besoins
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo: " + e.getMessage());
            // En cas d'erreur, on crée un placeholder
            logoView = new ImageView();
            logoView.setFitHeight(40);
        }
        
        // Section gauche avec Exit et titre
        HBox leftSection = new HBox();
        leftSection.setAlignment(Pos.CENTER_LEFT);
        leftSection.setSpacing(20);
        leftSection.getChildren().addAll(closeButton, titleLabel);
        
        // Logo centré par rapport à l'écran entier
        StackPane logoContainer = new StackPane();
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.getChildren().add(logoView);
        
        // Utilisation d'un StackPane pour superposer les éléments
        StackPane headerPane = new StackPane();
        headerPane.getChildren().addAll(logoContainer, leftSection);
        StackPane.setAlignment(leftSection, Pos.CENTER_LEFT);
        headerPane.getStyleClass().add("app-header");
        headerPane.setPadding(new Insets(15, 10, 15, 10));
        
        root.setTop(headerPane);
        
        tabPane = new TabPane();
        tabPane.getStyleClass().add("modern-tab-pane");
        tabPane.setPadding(new Insets(10));
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(2);
        tabPane.setEffect(dropShadow);
        
        HBox customTabBar = new HBox();
        customTabBar.getStyleClass().add("custom-tab-bar");
        customTabBar.setPadding(new Insets(5));
        customTabBar.setSpacing(5);
        
        HBox utilityTabs = new HBox();
        utilityTabs.setSpacing(0);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        HBox aboutTabContainer = new HBox();
        
        customTabBar.getChildren().addAll(utilityTabs, spacer, aboutTabContainer);
        
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("hidden-tabs");
        
        BorderPane tabContainer = new BorderPane();
        tabContainer.setTop(customTabBar);
        tabContainer.setCenter(tabPane);
        
        root.setCenter(tabContainer);
        
        tabPane.getTabs().addListener((javafx.collections.ListChangeListener<Tab>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Tab tab : change.getAddedSubList()) {
                        Button tabButton = new Button(tab.getText());
                        tabButton.getStyleClass().add("tab-button");
                        tabButton.setOnAction(e -> tabPane.getSelectionModel().select(tab));
                        
                        if (tab.getText().equals("About")) {
                            aboutTabContainer.getChildren().clear();
                            aboutTabContainer.getChildren().add(tabButton);
                        } else {
                            utilityTabs.getChildren().add(tabButton);
                        }
                        
                        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                            for (Node node : utilityTabs.getChildren()) {
                                node.getStyleClass().remove("active-tab");
                            }
                            for (Node node : aboutTabContainer.getChildren()) {
                                node.getStyleClass().remove("active-tab");
                            }
                            if (newTab != null) {
                                if (newTab.getText().equals("About")) {
                                    aboutTabContainer.getChildren().get(0).getStyleClass().add("active-tab");
                                } else {
                                    for (Node node : utilityTabs.getChildren()) {
                                        if (((Button) node).getText().equals(newTab.getText())) {
                                            node.getStyleClass().add("active-tab");
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    public Tab addTab(String title, Node content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        return tab;
    }
    
    public BorderPane getRoot() {
        return root;
    }
    
    public TabPane getTabPane() {
        return tabPane;
    }
}