package skymc.view.components;

import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;

public class ColorPickerView {
    private VBox root;
    private ColorPicker colorPicker;

    public ColorPickerView() {
        root = new VBox(10);
        colorPicker = new ColorPicker();
        colorPicker.setValue(javafx.scene.paint.Color.WHITE);
        root.getChildren().add(colorPicker);
    }

    public VBox getRoot() {
        return root;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
}