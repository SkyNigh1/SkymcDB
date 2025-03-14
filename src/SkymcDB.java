import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.swing.colorchooser.AbstractColorChooserPanel;


public class SkymcDB {
    private static final String dossierTextures ="textures";
    private static List<Bloc> baseBlocs = new ArrayList<>();
    private static JComboBox<String> comboBoxBlocDepart;
    private static JComboBox<String> comboBoxBlocFin;
    private static JPanel panelDegrade;
    private static JLabel numberLabel;
    private static JSpinner spinnerNombreBlocs;
    private static JLabel imageBlocDepart;
    private static JLabel imageBlocFin;

    
    // Couleurs personnalisées
    private static final Color BACKGROUND_COLOR = new Color(209, 213, 219);  // Gris clair
    private static final Color PANEL_COLOR = new Color(75, 85, 99, 128);     // Gris foncé transparent
    private static final Color HEADER_COLOR = new Color(31, 41, 55);         // Gris très foncé


    private static void styleButton(JButton button) {
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(20, 20));
    }

    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        baseBlocs = chargerBlocsDepuisTextures(dossierTextures);
    
        JFrame frame = new JFrame("SkymcDB");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setLayout(new BorderLayout(20, 20));
    
        // Panel principal avec padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Header
        JPanel headerPanel = createRoundedPanel(HEADER_COLOR);
        JLabel titleLabel = new JLabel("SkymcDB", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Panel de sélection
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        selectionPanel.setOpaque(false);
    
        // Bloc de départ
        JPanel startPanel = createBlockSelector("yellow_wool", true);
    
        // Nombre de blocs
        JPanel numberPanel = createNumberSelector();
    
        // Bloc de fin
        JPanel endPanel = createBlockSelector("red_wool", false);
    
        selectionPanel.add(startPanel);
        selectionPanel.add(numberPanel);
        selectionPanel.add(endPanel);
        mainPanel.add(selectionPanel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Bouton Générer
        JButton generateButton = createGenerateButton();
        JPanel generatePanel = new JPanel();
        generatePanel.setOpaque(false);
        generatePanel.add(generateButton);
        mainPanel.add(generatePanel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Panel du dégradé
        panelDegrade = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PANEL_COLOR);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        panelDegrade.setOpaque(false);
        mainPanel.add(panelDegrade);
    
        // Création du JTabbedPane pour les onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gradient", mainPanel);
    
        // Onglet de conversion HEX -> Bloc
        JPanel hexToBlockPanel = createHexToBlockPanel();
        tabbedPane.addTab("Hex to Block", hexToBlockPanel);
    
        // Onglet de génération de Pixel Art
        JPanel pixelArtPanel = createPixelArtPanel(); // Ce sera l'onglet pour la génération de pixel art
        tabbedPane.addTab("Image to Pixel Art", pixelArtPanel);
    
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null); // Centrer la fenêtre
        frame.setVisible(true);
    }
    
    

    public static String[] getNomBlocs(List<Bloc> blocs) {
        String[] noms = new String[blocs.size()];
        for (int i = 0; i < blocs.size(); i++) {
            noms[i] = blocs.get(i).getNom();
        }
        return noms;
    }

    
    private static JPanel createRoundedPanel(Color backgroundColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(backgroundColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel createBlockSelector(String defaultBlock, boolean isStart) {
        JPanel panel = createRoundedPanel(PANEL_COLOR);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setPreferredSize(new Dimension(250, 60));
    
        // Image du bloc
        JLabel imageLabel = new JLabel();
        mettreAJourImageBloc(imageLabel, defaultBlock);
    
        // ComboBox pour la sélection
        JComboBox<String> comboBox;
        if (isStart) {
            comboBoxBlocDepart = new JComboBox<>(getNomBlocs(baseBlocs));
            comboBoxBlocDepart.setSelectedItem(defaultBlock);
            comboBox = comboBoxBlocDepart;
            imageBlocDepart = imageLabel;  // Associer l'image du départ
        } else {
            comboBoxBlocFin = new JComboBox<>(getNomBlocs(baseBlocs));
            comboBoxBlocFin.setSelectedItem(defaultBlock);
            comboBox = comboBoxBlocFin;
            imageBlocFin = imageLabel;  // Associer l'image de fin
        }
    
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(PANEL_COLOR);
        comboBox.setBorder(null);
        comboBox.setFont(new Font("Arial", Font.BOLD, 14));
    
        // Ajouter un écouteur d'événements pour changer l'image
        comboBox.addActionListener(e -> {
            String selectedBloc = (String) comboBox.getSelectedItem();
            mettreAJourImageBloc(imageLabel, selectedBloc);
        });
    
        // Ajouter tout au panel
        panel.add(imageLabel);
        panel.add(comboBox);
        return panel;
    }
    
    private static void mettreAJourImageBloc(JLabel label, String blocNom) {
        try {
            BufferedImage img = ImageIO.read(new File(dossierTextures + "/" + blocNom + ".png"));
            Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            label.setIcon(null); // Si l'image ne se charge pas, on la retire
            label.setText("IMG"); // On affiche "IMG" à la place
        }
    }
    

    private static JPanel createNumberSelector() {
        JPanel panel = createRoundedPanel(PANEL_COLOR);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setPreferredSize(new Dimension(100, 60));
    
        // Création du spinner
        spinnerNombreBlocs = new JSpinner(new SpinnerNumberModel(4, 2, 32, 1)); // Valeur initiale 4, min 2, max 10, incrément 1
        spinnerNombreBlocs.setFont(new Font("Arial", Font.BOLD, 20));
        spinnerNombreBlocs.setPreferredSize(new Dimension(60, 30));
    
        panel.add(spinnerNombreBlocs);
        return panel;
    }
 

    private static JButton createGenerateButton() {
        JButton button = new JButton("Generate Gradient") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_COLOR);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> genererDegrade());
        
        return button;
    }

    private static void genererDegrade() {
        // Récupérer les blocs sélectionnés
        Bloc blocDepart = baseBlocs.get(comboBoxBlocDepart.getSelectedIndex());
        Bloc blocFin = baseBlocs.get(comboBoxBlocFin.getSelectedIndex());
        
        // Générer le dégradé using numberLabel's value instead of spinner
        int nombreDeGradients = (int) spinnerNombreBlocs.getValue();
        List<Color> couleursIntermediaires = genererCouleursIntermediaires(
            blocDepart.getCouleur(), 
            blocFin.getCouleur(), 
            nombreDeGradients
        );
        
        List<Bloc> degrade = trouverBlocsLesPlusProches(baseBlocs, couleursIntermediaires);
        afficherDegrade(degrade, panelDegrade);
    }

    public static List<Bloc> chargerBlocsDepuisTextures(String dossierPath) {
        List<Bloc> blocs = new ArrayList<>();
        File dossier = new File(dossierPath);

        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : le dossier " + dossierPath + " n'existe pas !");
            return blocs;
        }

        File[] fichiers = dossier.listFiles((dir, name) -> name.endsWith(".png"));
        if (fichiers == null) return blocs;

        for (File fichier : fichiers) {
            try {
                BufferedImage image = ImageIO.read(fichier);
                Color couleurMoyenne = calculerCouleurMoyenne(image);
                String nomBloc = fichier.getName().replace(".png", ""); // Retirer .png
                blocs.add(new Bloc(nomBloc, String.format("#%02X%02X%02X", 
                    couleurMoyenne.getRed(), couleurMoyenne.getGreen(), couleurMoyenne.getBlue()), image));
            } catch (IOException e) {
                System.out.println("Impossible de lire " + fichier.getName());
            }
        }
        return blocs;
    }

    public static Color calculerCouleurMoyenne(BufferedImage image) {
        long sommeRouge = 0, sommeVert = 0, sommeBleu = 0;
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        int totalPixels = largeur * hauteur;
    
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                Color couleur = new Color(image.getRGB(x, y), true);
                sommeRouge += couleur.getRed();
                sommeVert += couleur.getGreen();
                sommeBleu += couleur.getBlue();
            }
        }
    
        return new Color((int) (sommeRouge / totalPixels), (int) (sommeVert / totalPixels), (int) (sommeBleu / totalPixels));
    }

    public static List<Color> genererCouleursIntermediaires(Color debut, Color fin, int nombreIntermediaires) {
        List<Color> resultats = new ArrayList<>();
    
        for (int i = 1; i <= nombreIntermediaires; i++) {
            float ratio = (float) i / (nombreIntermediaires + 1);
            int rouge = (int) (debut.getRed() + ratio * (fin.getRed() - debut.getRed()));
            int vert = (int) (debut.getGreen() + ratio * (fin.getGreen() - debut.getGreen()));
            int bleu = (int) (debut.getBlue() + ratio * (fin.getBlue() - debut.getBlue()));
    
            resultats.add(new Color(rouge, vert, bleu));
        }
    
        return resultats;
    }

    public static List<Bloc> trouverBlocsLesPlusProches(List<Bloc> baseBlocs, List<Color> couleursCibles) {
        List<Bloc> resultats = new ArrayList<>();

        for (Color couleur : couleursCibles) {
            Bloc blocLePlusProche = null;
            double distanceMin = Double.MAX_VALUE;

            for (Bloc bloc : baseBlocs) {
                double distance = distanceCouleur(couleur, bloc.getCouleur());
                if (distance < distanceMin) {
                    distanceMin = distance;
                    blocLePlusProche = bloc;
                }
            }

            if (blocLePlusProche != null) {
                resultats.add(blocLePlusProche);
            }
        }

        return resultats;
    }

    public static double distanceCouleur(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static void afficherDegrade(List<Bloc> degrade, JPanel panelDegrade) {
        panelDegrade.removeAll();
    
        // Ajouter le bloc de départ au début
        Bloc blocDepart = baseBlocs.get(comboBoxBlocDepart.getSelectedIndex());
        panelDegrade.add(creerImageLabel(blocDepart));
    
        // Ajouter les blocs intermédiaires du dégradé
        for (Bloc bloc : degrade) {
            panelDegrade.add(creerImageLabel(bloc));
        }
    
        // Ajouter le bloc de fin à la fin
        Bloc blocFin = baseBlocs.get(comboBoxBlocFin.getSelectedIndex());
        panelDegrade.add(creerImageLabel(blocFin));
    
        panelDegrade.revalidate();
        panelDegrade.repaint();
    }
    
    // Fonction pour créer un JLabel avec une image redimensionnée
    private static JLabel creerImageLabel(Bloc bloc) {
        ImageIcon icon = new ImageIcon(bloc.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
        return new JLabel(icon);
    }
        
    private static JPanel createHexToBlockPanel() {
        // Panel principal avec padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Header
        JPanel headerPanel = createRoundedPanel(HEADER_COLOR);
        JLabel titleLabel = new JLabel("Hex to Block Converter", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Panel central pour la sélection de couleur
        JPanel colorPanel = createRoundedPanel(PANEL_COLOR);
        colorPanel.setLayout(new BorderLayout(10, 10));
        colorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Création d'un JColorChooser personnalisé
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setPreviewPanel(new JPanel()); // Supprimer le panneau de prévisualisation
    
        // Ajout du sélecteur de couleur
        colorPanel.add(colorChooser, BorderLayout.CENTER);
        mainPanel.add(colorPanel);
        mainPanel.add(Box.createVerticalStrut(20));
    
        // Panel inférieur pour le code hex et l'affichage du bloc
        JPanel bottomPanel = createRoundedPanel(PANEL_COLOR);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Champ hex
        JPanel hexPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hexPanel.setOpaque(false);
        JLabel hexLabel = new JLabel("Hex Code:");
        hexLabel.setForeground(Color.WHITE);
        hexLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField hexField = new JTextField(7);
        hexField.setFont(new Font("Arial", Font.PLAIN, 16));
        hexPanel.add(hexLabel);
        hexPanel.add(hexField);
    
        // Label pour le bloc
        JPanel blockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        blockPanel.setOpaque(false);
        JLabel blockLabel = new JLabel();
        blockLabel.setPreferredSize(new Dimension(64, 64));
        blockPanel.add(blockLabel);
    
        // Label pour le nom du bloc
        JLabel blockNameLabel = new JLabel();
        blockNameLabel.setForeground(Color.WHITE);
        blockNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        blockPanel.add(blockNameLabel);
    
        bottomPanel.add(hexPanel);
        bottomPanel.add(blockPanel);
        mainPanel.add(bottomPanel);
    
        // Écouteur de changement de couleur
        colorChooser.getSelectionModel().addChangeListener(e -> {
            Color selectedColor = colorChooser.getColor();
            String hexCode = String.format("#%02X%02X%02X", 
                selectedColor.getRed(), 
                selectedColor.getGreen(), 
                selectedColor.getBlue());
            hexField.setText(hexCode);
            Bloc closestBlock = findClosestBlockByColor(selectedColor);
            updateBlockDisplay(selectedColor, blockLabel, blockNameLabel, closestBlock);
        });
    
        // Écouteur pour le champ hex
        hexField.addActionListener(e -> {
            try {
                String hexText = hexField.getText();
                if (!hexText.startsWith("#")) {
                    hexText = "#" + hexText;
                }
                Color hexColor = Color.decode(hexText);
                colorChooser.setColor(hexColor);
                Bloc closestBlock = findClosestBlockByColor(hexColor);
                updateBlockDisplay(hexColor, blockLabel, blockNameLabel, closestBlock);
            } catch (NumberFormatException ex) {
                blockLabel.setIcon(null);
                blockLabel.setText("Invalid Hex");
                blockNameLabel.setText("");
            }
        });
    
        return mainPanel;
    }

    private static double calculateColorSimilarity(Color c1, Color c2) {
        double distance = colorDistance(c1, c2);
        // La distance maximale possible est sqrt(255²+255²+255²) ≈ 441.67
        // Convertir la distance en similarité (0 à 1)
        return Math.max(0, 1 - (distance / 441.67));
    }
        
        
    private static void updateBlockDisplay(Color color, JLabel blockLabel, JLabel nameLabel, Bloc closestBlock) {
        if (closestBlock != null) {
            // Mettre à jour l'image du bloc
            Image scaledImage = closestBlock.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            blockLabel.setIcon(new ImageIcon(scaledImage));
            blockLabel.setText("");
            
            // Mettre à jour le nom du bloc
            nameLabel.setText(closestBlock.getNom());
            
            // Calculer la similarité en pourcentage
            double similarity = calculateColorSimilarity(color, closestBlock.getCouleur());
            nameLabel.setToolTipText(String.format("Similarity: %.1f%%", similarity * 100));
        } else {
            blockLabel.setIcon(null);
            blockLabel.setText("No match");
            nameLabel.setText("");
        }
    }
        
    private static Bloc findClosestBlockByColor(Color targetColor) {
        if (baseBlocs.isEmpty()) {
            return null;
        }
    
        Bloc closestBlock = null;
        double minDistance = Double.MAX_VALUE;
    
        for (Bloc bloc : baseBlocs) {
            double distance = colorDistance(targetColor, bloc.getCouleur());
            if (distance < minDistance) {
                minDistance = distance;
                closestBlock = bloc;
            }
        }
    
        return closestBlock;
    }
        
    private static double colorDistance(Color c1, Color c2) {
        // Utilisation de la distance euclidienne pondérée dans l'espace RGB
        // Les facteurs de pondération sont basés sur la perception humaine des couleurs
        double weightR = 0.3;
        double weightG = 0.59;
        double weightB = 0.11;
    
        double dr = (c1.getRed() - c2.getRed()) * weightR;
        double dg = (c1.getGreen() - c2.getGreen()) * weightG;
        double db = (c1.getBlue() - c2.getBlue()) * weightB;
    
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }
        
    private static void generatePixelArt(String imagePath, int numBlocksHigh) {
        try {
            // Limiter la hauteur maximale à 128 blocs
            if (numBlocksHigh > 128) {
                JOptionPane.showMessageDialog(null, 
                    "The maximum allowed block height is 128.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Logs pour le débogage
            System.out.println("Image path: " + imagePath);
            System.out.println("Block height requested (in blocks): " + numBlocksHigh);
            
            // Vérification du fichier et lecture de l'image
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                JOptionPane.showMessageDialog(null, 
                    "Image file not found: " + imagePath, 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                JOptionPane.showMessageDialog(null, 
                    "Unable to read image file. Your file is probably corrupted.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Calculer le nombre de blocs en largeur pour conserver le ratio de l'image
            double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
            int numBlocksWide = (int) Math.round(numBlocksHigh * ratio);
            System.out.println("Pixel art will be " + numBlocksWide + " blocks wide and " + numBlocksHigh + " blocks high.");
            
            // Définir la taille d'affichage de chaque bloc en pixels (2x plus petit que 32 => 16)
            int blockSize = 16;
            
            // Créer une version réduite de l'image, où chaque pixel représente un bloc
            BufferedImage smallImage = new BufferedImage(numBlocksWide, numBlocksHigh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gSmall = smallImage.createGraphics();
            gSmall.drawImage(originalImage, 0, 0, numBlocksWide, numBlocksHigh, null);
            gSmall.dispose();
            
            // Créer l'image composite finale aux dimensions (numBlocksWide*blockSize) x (numBlocksHigh*blockSize)
            int finalWidth = numBlocksWide * blockSize;
            int finalHeight = numBlocksHigh * blockSize;
            BufferedImage finalImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gFinal = finalImage.createGraphics();
            
            // Pour chaque pixel (qui représente un bloc) de l'image réduite :
            for (int y = 0; y < numBlocksHigh; y++) {
                for (int x = 0; x < numBlocksWide; x++) {
                    int rgb = smallImage.getRGB(x, y);
                    Color pixelColor = new Color(rgb, true);
                    // On ignore les pixels transparents
                    if (pixelColor.getAlpha() > 128) {
                        Bloc closestBlock = findClosestBlockByColor(pixelColor);
                        if (closestBlock != null && closestBlock.getImage() != null) {
                            // Récupérer la texture du bloc et la redimensionner à la taille d'un bloc
                            Image blockTexture = closestBlock.getImage().getScaledInstance(blockSize, blockSize, Image.SCALE_SMOOTH);
                            gFinal.drawImage(blockTexture, x * blockSize, y * blockSize, null);
                        } else {
                            // Si aucun bloc n'est trouvé, on remplit avec la couleur
                            gFinal.setColor(pixelColor);
                            gFinal.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                        }
                    } else {
                        // Pour les zones transparentes, remplir avec une couleur par défaut (ici, gris foncé)
                        gFinal.setColor(Color.DARK_GRAY);
                        gFinal.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                    }
                }
            }
            gFinal.dispose();
            
            // Afficher l'image composite dans une fenêtre
            JFrame frame = new JFrame("Minecraft Pixel Art");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JLabel imageLabel = new JLabel(new ImageIcon(finalImage));
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            frame.add(scrollPane);
            
            // Taille initiale de la fenêtre
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("Pixel art generated and window displayed successfully.");
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error processing image: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Unexpected error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
        

    private static void displayPixelArt(BufferedImage pixelArtImage) {
        // Cette méthode pourrait afficher l'image sous forme de blocs Minecraft (ou carrés colorés)
        // Par exemple, ajouter des petits panneaux ou des JLabels représentant des blocs Minecraft
        JFrame frame = new JFrame("Pixel Art");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(pixelArtImage.getHeight(), pixelArtImage.getWidth()));
        
        for (int y = 0; y < pixelArtImage.getHeight(); y++) {
            for (int x = 0; x < pixelArtImage.getWidth(); x++) {
                int pixelColor = pixelArtImage.getRGB(x, y);
                JPanel block = new JPanel();
                block.setBackground(new Color(pixelColor));
                panel.add(block);
            }
        }
    
        frame.add(panel);
        frame.setSize(800, 600);  // Ajuste la taille selon le pixel art
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }    

    private static JPanel createPixelArtPanel() {
        // Panneau principal avec BorderLayout
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Titre
        JLabel titleLabel = new JLabel("Convert Image to Pixel Art", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel headerPanel = createRoundedPanel(HEADER_COLOR);
        headerPanel.add(titleLabel);
        panel.add(headerPanel, BorderLayout.NORTH);
    
        // Panneau central pour la sélection de fichier
        JPanel filePanel = createRoundedPanel(PANEL_COLOR);
        filePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton selectImageButton = new JButton("Select Image") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_COLOR);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }
        };
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectImageButton.setOpaque(false);
        selectImageButton.setContentAreaFilled(false);
        selectImageButton.setBorderPainted(false);
        selectImageButton.setFocusPainted(false);
        
        JTextField imagePathField = new JTextField(30);
        imagePathField.setEditable(false);
        imagePathField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        filePanel.add(selectImageButton);
        filePanel.add(imagePathField);
        panel.add(filePanel, BorderLayout.CENTER);
    
        // Panneau inférieur pour la hauteur et le bouton de génération
        JPanel bottomPanel = createRoundedPanel(PANEL_COLOR);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Section hauteur
        JLabel heightLabel = new JLabel("Block Height:");
        heightLabel.setForeground(Color.WHITE);
        heightLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField heightField = new JTextField(5);
        heightField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Bouton de génération
        JButton generateButton = new JButton("Generate Pixel Art") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_COLOR);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }
        };
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setOpaque(false);
        generateButton.setContentAreaFilled(false);
        generateButton.setBorderPainted(false);
        generateButton.setFocusPainted(false);
    
        // Ajouter les composants au panneau inférieur
        bottomPanel.add(heightLabel);
        bottomPanel.add(heightField);
        bottomPanel.add(generateButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
    
        // Gestionnaire d'événements pour le bouton de sélection d'image
        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "Image Files", "png", "jpg", "jpeg"));
            
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        });
    
        // Gestionnaire d'événements pour le bouton de génération
        generateButton.addActionListener(e -> {
            String imagePath = imagePathField.getText();
            String heightText = heightField.getText();
    
            if (imagePath.isEmpty() || heightText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, 
                    "Please provide both image and height!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                int height = Integer.parseInt(heightText);
                if (height <= 0) {
                    JOptionPane.showMessageDialog(panel, 
                        "Height must be a positive number!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                generatePixelArt(imagePath, height);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Invalid height value! Please enter a valid number.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }
}
