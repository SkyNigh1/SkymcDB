package skymc.controller;

import javafx.collections.FXCollections;
import skymc.model.Block;
import skymc.model.PatternGenerator;
import skymc.model.TextureManager;
import skymc.view.PatternView;

public class PatternController {
    private PatternView view;
    private PatternGenerator model;

    public PatternController(PatternView view, PatternGenerator model) {
        this.view = view;
        this.model = model;
        initializeListeners();
        initializeBlockComboBoxes();
    }

    private void initializeListeners() {
        view.getGenerateButton().setOnAction(event -> generatePattern());
    }

    private void initializeBlockComboBoxes() {
        TextureManager textureManager = TextureManager.getInstance();
        view.getStartBlockComboBox().setItems(FXCollections.observableArrayList(textureManager.getAllBlocks()));
        view.getEndBlockComboBox().setItems(FXCollections.observableArrayList(textureManager.getAllBlocks()));
    }

    private void generatePattern() {
        Block startBlock = view.getStartBlockComboBox().getValue();
        Block endBlock = view.getEndBlockComboBox().getValue();
        int size = (int) view.getSizeSlider().getValue();
        double noiseLevel = view.getNoiseSlider().getValue();
        PatternGenerator.PatternType patternType = view.getPatternTypeComboBox().getValue();
        PatternGenerator.CurveType curveType = view.getCurveTypeComboBox().getValue();
        boolean useTopTexture = view.isUseTopTextureSelected();
    
        Block[][] pattern = model.generatePattern(startBlock, endBlock, size, noiseLevel, patternType, curveType, useTopTexture);
        view.displayPattern(pattern);
    }
}
