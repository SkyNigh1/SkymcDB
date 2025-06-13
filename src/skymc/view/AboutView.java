package skymc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class AboutView {
    private final BorderPane root;

    public AboutView() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("About SkyMC Builder Tools");
        titleLabel.getStyleClass().add("title-label");

        VBox mainContainer = new VBox(10);
        mainContainer.setAlignment(Pos.TOP_CENTER); // Titre centré, contenu aligné à gauche
        mainContainer.getStyleClass().add("LinearContainer");
        root.setCenter(mainContainer);

        // Software Information
        Label infoTitle = new Label("Software Information");
        infoTitle.getStyleClass().add("section-label");

        Label createdBy = new Label("Created and developed by SkyNight");
        createdBy.getStyleClass().add("info-label");
        ImageView skyNightPP = createImageView("file:assets/images/skynight.png");

        Label thanksMax = new Label("Thanks to MaxLananas for pattern algorithms");
        thanksMax.getStyleClass().add("info-label");
        ImageView maxLananasPP = createImageView("file:assets/images/maxlananas.png");

        Label collabPotager = new Label("In collaboration with Potager Builds");
        collabPotager.getStyleClass().add("info-label");
        ImageView potagerPP = createImageView("file:assets/images/potager.png");

        Label paletteCredits = new Label("Palette credits: Zoron & Seijayo");
        paletteCredits.getStyleClass().add("info-label");

        Label version = new Label("Version: 1.4");
        version.getStyleClass().add("info-label");

        Label minecraftVersion = new Label("Minecraft Version: 1.21.4");
        minecraftVersion.getStyleClass().add("info-label");

        HBox skyNightBox = new HBox(10, skyNightPP, createdBy);
        skyNightBox.setAlignment(Pos.CENTER_LEFT);
        HBox maxLananasBox = new HBox(10, maxLananasPP, thanksMax);
        maxLananasBox.setAlignment(Pos.CENTER_LEFT);
        HBox potagerBox = new HBox(10, potagerPP, collabPotager);
        potagerBox.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5, infoTitle, skyNightBox, maxLananasBox, potagerBox, paletteCredits, version, minecraftVersion);
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.getStyleClass().add("label-result-pane");

        // Social Links
        Label linksTitle = new Label("Connect with Us");
        linksTitle.getStyleClass().add("section-label");

        Hyperlink skyNightLink = new Hyperlink("SkyNight's Website");
        skyNightLink.setOnAction(e -> openLink("https://skynightbuilds.com/"));
        skyNightLink.getStyleClass().add("hyperlink");

        Hyperlink maxLananasLink = new Hyperlink("MaxLananas's Website");
        maxLananasLink.setOnAction(e -> openLink("https://maxlananas-builds.pages.dev/"));
        maxLananasLink.getStyleClass().add("hyperlink");

        Hyperlink potagerDiscordLink = new Hyperlink("Potager Builds Discord");
        potagerDiscordLink.setOnAction(e -> openLink("https://discord.gg/x4aPUXzNqY"));
        potagerDiscordLink.getStyleClass().add("hyperlink");

        Hyperlink zoronLink = new Hyperlink("Zoron's Instagram");
        zoronLink.setOnAction(e -> openLink("https://www.instagram.com/zoron117/"));
        zoronLink.getStyleClass().add("hyperlink");

        Hyperlink seijayoLink = new Hyperlink("Seijayo's Instagram");
        seijayoLink.setOnAction(e -> openLink("https://www.instagram.com/seijayo/"));
        seijayoLink.getStyleClass().add("hyperlink");

        Hyperlink githubRepoLink = new Hyperlink("SkyMC Builder Tools GitHub");
        githubRepoLink.setOnAction(e -> openLink("https://github.com/SkyNigh1/SkymcDB"));
        githubRepoLink.getStyleClass().add("hyperlink");

        VBox linksBox = new VBox(5, linksTitle, skyNightLink, maxLananasLink, potagerDiscordLink, zoronLink, seijayoLink, githubRepoLink);
        linksBox.setAlignment(Pos.TOP_LEFT);
        linksBox.getStyleClass().add("label-result-pane");

        // Legal Information
        Label legalTitle = new Label("Legal Information");
        legalTitle.getStyleClass().add("section-label");

        Label copyright = new Label("© 2025 SkyNight. All rights reserved.");
        copyright.getStyleClass().add("info-label");

        Label license = new Label("License: MIT License");
        license.getStyleClass().add("info-label");

        Hyperlink licenseLink = new Hyperlink("View License on GitHub");
        licenseLink.setOnAction(e -> openLink("https://github.com/SkyNigh1/SkymcDB/blob/main/LICENSE"));
        licenseLink.getStyleClass().add("hyperlink");

        Label privacy = new Label("No user data is collected by SkyMC Builder Tools.");
        privacy.getStyleClass().add("info-label");

        Label thirdParty = new Label("Third-party libraries: JavaFX (OpenJFX License), Jansi (Apache 2.0 License).");
        thirdParty.getStyleClass().add("info-label");

        VBox legalBox = new VBox(5, legalTitle, copyright, license, licenseLink, privacy, thirdParty);
        legalBox.setAlignment(Pos.TOP_LEFT);
        legalBox.getStyleClass().add("label-result-pane");

        mainContainer.getChildren().addAll(titleLabel, infoBox, linksBox, legalBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    private void openLink(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            System.out.println("Opening link: " + url);
        } catch (Exception e) {
            System.err.println("Failed to open link: " + url + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ImageView createImageView(String imagePath) {
        Image image;
        try {
            image = new Image(imagePath, 48, 48, false, false);
            if (image.isError()) {
                throw new Exception("Image failed to load");
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath + " - " + e.getMessage());
            image = new Image("file:assets/images/placeholder.png", 48, 48, false, false);
            if (image.isError()) {
                System.err.println("Failed to load placeholder image: file:assets/images/placeholder.png");
                image = null;
            }
        }
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add("pixel-perfect");
        return imageView;
    }
}