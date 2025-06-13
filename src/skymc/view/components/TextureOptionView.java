package skymc.view.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TextureOptionView {
    private VBox root;
    private CheckBox useTopTextureCheckBox;
    private CheckBox useSideTextureCheckBox;

    public TextureOptionView() {
        root = new VBox(5);
        useTopTextureCheckBox = new CheckBox("Use the top texture");
        useSideTextureCheckBox = new CheckBox("Use the side texture");
        useTopTextureCheckBox.setSelected(true);
        root.getChildren().addAll(new Label("Options"), useTopTextureCheckBox, useSideTextureCheckBox);
    }

    public VBox getRoot() {
        return root;
    }

    public CheckBox getUseTopTextureCheckBox() {
        return useTopTextureCheckBox;
    }

    public CheckBox getUseSideTextureCheckBox() {
        return useSideTextureCheckBox;
    }
}