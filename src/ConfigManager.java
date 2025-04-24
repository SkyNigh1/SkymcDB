import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Gère la configuration et les préférences de l'application.
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;
    
    private static ConfigManager instance;
    
    /**
     * Obtient l'instance unique du ConfigManager.
     * 
     * @return L'instance de ConfigManager
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Constructeur privé pour le singleton.
     */
    private ConfigManager() {
        properties = new Properties();
        loadConfig();
    }
    
    /**
     * Charge la configuration depuis le fichier.
     */
    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
                // Utiliser les valeurs par défaut
                setDefaultValues();
            }
        } else {
            // Fichier de configuration non trouvé, utiliser les valeurs par défaut
            setDefaultValues();
            saveConfig();
        }
    }
    
    /**
     * Définit les valeurs par défaut de la configuration.
     */
    private void setDefaultValues() {
        properties.setProperty("useTopTexture", "true");
        properties.setProperty("lastDirectory", System.getProperty("user.home"));
        properties.setProperty("gradientSteps", "5");
        properties.setProperty("pixelArtHeight", "32");
    }
    
    /**
     * Enregistre la configuration dans le fichier.
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "SkymcDB Configuration");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement de la configuration: " + e.getMessage());
        }
    }
    
    /**
     * Obtient une propriété sous forme de chaîne.
     * 
     * @param key Clé de la propriété
     * @param defaultValue Valeur par défaut si la propriété n'existe pas
     * @return La valeur de la propriété
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Obtient une propriété sous forme d'entier.
     * 
     * @param key Clé de la propriété
     * @param defaultValue Valeur par défaut si la propriété n'existe pas
     * @return La valeur de la propriété
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtient une propriété sous forme de booléen.
     * 
     * @param key Clé de la propriété
     * @param defaultValue Valeur par défaut si la propriété n'existe pas
     * @return La valeur de la propriété
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    /**
     * Définit une propriété.
     * 
     * @param key Clé de la propriété
     * @param value Valeur de la propriété
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }
    
    /**
     * Définit une propriété entière.
     * 
     * @param key Clé de la propriété
     * @param value Valeur de la propriété
     */
    public void setIntProperty(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
        saveConfig();
    }
    
    /**
     * Définit une propriété booléenne.
     * 
     * @param key Clé de la propriété
     * @param value Valeur de la propriété
     */
    public void setBooleanProperty(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        saveConfig();
    }
}