import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Composant d'interface utilisateur pour la sélection d'une couleur.
 */
public class ColorPicker extends JPanel implements ChangeListener {
    private final JSlider redSlider, greenSlider, blueSlider;
    private final JSlider hueSlider, saturationSlider, valueSlider;
    private final JPanel colorPreview;
    private final JPanel rgbPanel, hsvPanel;
    private final JTabbedPane tabbedPane;
    private final JTextField hexField;
    private final JButton pickButton;
    private Color currentColor = Color.WHITE;
    private final Consumer<Color> onColorSelected;
    private boolean updating = false;
    
    /**
     * Constructeur.
     * 
     * @param onColorSelected Callback appelé lorsqu'une couleur est sélectionnée
     */
    public ColorPicker(Consumer<Color> onColorSelected) {
        this.onColorSelected = onColorSelected;
        
        setLayout(new BorderLayout(10, 10));
        
        // Panel de prévisualisation de la couleur
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(100, 100));
        colorPreview.setBackground(currentColor);
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(colorPreview, BorderLayout.EAST);
        
        // Tabbed pane pour RGB et HSV
        tabbedPane = new JTabbedPane();
        
        // Panel RGB
        rgbPanel = new JPanel();
        rgbPanel.setLayout(new GridLayout(3, 2, 10, 10));
        
        redSlider = createSlider(0, 255, currentColor.getRed());
        greenSlider = createSlider(0, 255, currentColor.getGreen());
        blueSlider = createSlider(0, 255, currentColor.getBlue());
        
        rgbPanel.add(new JLabel("Rouge:"));
        rgbPanel.add(redSlider);
        rgbPanel.add(new JLabel("Vert:"));
        rgbPanel.add(greenSlider);
        rgbPanel.add(new JLabel("Bleu:"));
        rgbPanel.add(blueSlider);
        
        tabbedPane.addTab("RGB", rgbPanel);
        
        // Panel HSV
        hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayout(3, 2, 10, 10));
        
        float[] hsv = ColorUtils.rgbToHsv(currentColor);
        
        hueSlider = createSlider(0, 360, (int) hsv[0]);
        saturationSlider = createSlider(0, 100, (int) hsv[1]);
        valueSlider = createSlider(0, 100, (int) hsv[2]);
        
        hsvPanel.add(new JLabel("Teinte:"));
        hsvPanel.add(hueSlider);
        hsvPanel.add(new JLabel("Saturation:"));
        hsvPanel.add(saturationSlider);
        hsvPanel.add(new JLabel("Valeur:"));
        hsvPanel.add(valueSlider);
        
        tabbedPane.addTab("HSV", hsvPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inférieur avec le champ HEX et le bouton
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        
        // Champ HEX
        hexField = new JTextField(getHexString(currentColor));
        hexField.setToolTipText("Code hexadécimal de la couleur");
        hexField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Color newColor = Color.decode("#" + hexField.getText().replaceAll("#", ""));
                    setColor(newColor);
                } catch (NumberFormatException ex) {
                    // Ignorer les entrées invalides
                    hexField.setText(getHexString(currentColor));
                }
            }
        });
        
        // Bouton de sélection
        pickButton = new JButton("Sélectionner");
        pickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(ColorPicker.this, "Choisir une couleur", currentColor);
                if (newColor != null) {
                    setColor(newColor);
                }
            }
        });
        
        bottomPanel.add(new JLabel("HEX:"), BorderLayout.WEST);
        bottomPanel.add(hexField, BorderLayout.CENTER);
        bottomPanel.add(pickButton, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Ajouter les listeners après avoir tout configuré
        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);
        hueSlider.addChangeListener(this);
        saturationSlider.addChangeListener(this);
        valueSlider.addChangeListener(this);
    }
    
    /**
     * Crée un slider avec les propriétés spécifiées.
     * 
     * @param min Valeur minimale
     * @param max Valeur maximale
     * @param value Valeur initiale
     * @return Le slider créé
     */
    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setMinorTickSpacing((max - min) / 10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }
    
    /**
     * Convertit une couleur en chaîne hexadécimale.
     * 
     * @param color La couleur à convertir
     * @return La chaîne hexadécimale correspondante
     */
    private String getHexString(Color color) {
        return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Définit la couleur actuelle et met à jour l'interface.
     * 
     * @param color La nouvelle couleur
     */
    public void setColor(Color color) {
        if (color == null) return;
        
        updating = true;
        this.currentColor = color;
        
        // Mettre à jour la prévisualisation
        colorPreview.setBackground(color);
        
        // Mettre à jour les sliders RGB
        redSlider.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
        
        // Mettre à jour les sliders HSV
        float[] hsv = ColorUtils.rgbToHsv(color);
        hueSlider.setValue((int) hsv[0]);
        saturationSlider.setValue((int) hsv[1]);
        valueSlider.setValue((int) hsv[2]);
        
        // Mettre à jour le champ HEX
        hexField.setText(getHexString(color));
        
        // Notifier le listener
        if (onColorSelected != null) {
            onColorSelected.accept(color);
        }
        
        updating = false;
    }
    
    /**
     * @return La couleur actuellement sélectionnée
     */
    public Color getColor() {
        return currentColor;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        if (updating) return;
        
        if (tabbedPane.getSelectedIndex() == 0) {
            // Mode RGB
            int r = redSlider.getValue();
            int g = greenSlider.getValue();
            int b = blueSlider.getValue();
            setColor(new Color(r, g, b));
        } else {
            // Mode HSV
            float h = hueSlider.getValue();
            float s = saturationSlider.getValue();
            float v = valueSlider.getValue();
            setColor(ColorUtils.hsvToRgb(h, s, v));
        }
    }
}