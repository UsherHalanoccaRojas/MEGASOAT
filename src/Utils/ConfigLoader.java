/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author USUARIO
 */
public class ConfigLoader {
    
    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());
    private static Properties properties;
    private static final String CONFIG_FILE = "config.properties";
    
    
    static {
        properties = new Properties();
        loadConfig();
    }
    
    /**
     * Carga la configuración desde el archivo properties
     */
    private static void loadConfig() {
        // Primero intentar cargar desde el classpath
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Configuración cargada desde classpath");
                return;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "No se pudo cargar configuración desde classpath: {0}", e.getMessage());
        }
        
        // Si no se encuentra en classpath, intentar desde el directorio actual
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            logger.info("Configuración cargada desde archivo: " + CONFIG_FILE);
        } catch (IOException e) {
            logger.log(Level.WARNING, "No se encontró archivo de configuración, usando valores por defecto: {0}", e.getMessage());
            setDefaultProperties();
        }
    }
    
    /**
     * Establece valores por defecto si no existe el archivo de configuración
     */
    private static void setDefaultProperties() {
        // Base de Datos
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/soat_sistema?useSSL=false&serverTimezone=America/Lima");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        
        // WhatsApp API
        properties.setProperty("whatsapp.enabled", "false");
        properties.setProperty("whatsapp.api.url", "https://api.whatsapp.com/v1");
        properties.setProperty("whatsapp.api.key", "");
        properties.setProperty("whatsapp.instance.id", "");
        properties.setProperty("whatsapp.phone.number", "");
        
        // OCR
        properties.setProperty("ocr.tessdata.path", "./tessdata");
        properties.setProperty("ocr.language", "spa");
        
        // Rutas del sistema
        properties.setProperty("pdf.storage.path", "./pdfs/");
        properties.setProperty("temp.files.path", "./temp/");
        properties.setProperty("backup.path", "./backups/");
        
        // Configuración de la aplicación
        properties.setProperty("app.name", "MegaSOAT");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.company", "MegaSOAT Solutions");
        
        // Comisiones por defecto
        properties.setProperty("default.commission.auto", "15");
        properties.setProperty("default.commission.moto", "10");
        properties.setProperty("default.commission.camion", "12");
        
        logger.info("Usando valores por defecto para la configuración");
    }
    
    /**
     * Obtiene todas las propiedades
     * @return Properties con toda la configuración
     */
    public static Properties load() {
        return properties;
    }
    
    /**
     * Obtiene una propiedad por su clave
     * @param key Clave de la propiedad
     * @return Valor de la propiedad o null si no existe
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Obtiene una propiedad por su clave con valor por defecto
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe
     * @return Valor de la propiedad o defaultValue si no existe
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Obtiene una propiedad como entero
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto
     * @return Valor entero de la propiedad
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene una propiedad como double
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto
     * @return Valor double de la propiedad
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene una propiedad como booleano
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto
     * @return Valor booleano de la propiedad
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key, String.valueOf(defaultValue));
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
    
    /**
     * Establece una propiedad en tiempo de ejecución
     * @param key Clave de la propiedad
     * @param value Valor de la propiedad
     */
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Guarda la configuración en un archivo
     * @param filePath Ruta donde guardar el archivo
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean saveConfig(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            properties.store(fos, "MegaSOAT Configuration File");
            logger.info("Configuración guardada en: " + filePath);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar configuración: {0}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Recarga la configuración desde el archivo
     */
    public static void reload() {
        properties.clear();
        loadConfig();
        logger.info("Configuración recargada");
    }
    
    /**
     * Obtiene la URL de conexión a la base de datos
     */
    public static String getDatabaseUrl() {
        return getProperty("db.url", "jdbc:mysql://localhost:3307/megasoat_db");
    }
    
    /**
     * Obtiene el usuario de la base de datos
     */
    public static String getDatabaseUser() {
        return getProperty("db.user", "root");
    }
    
    /**
     * Obtiene la contraseña de la base de datos
     */
    public static String getDatabasePassword() {
        return getProperty("db.password", "");
    }
    
    /**
     * Verifica si WhatsApp está habilitado
     */
    public static boolean isWhatsAppEnabled() {
        return getBooleanProperty("whatsapp.enabled", false);
    }
    
    /**
     * Obtiene la ruta de almacenamiento de PDFs
     */
    public static String getPdfStoragePath() {
        return getProperty("pdf.storage.path", "./pdfs/");
    }
    
    /**
     * Obtiene el porcentaje de comisión por defecto para autos
     */
    public static double getDefaultAutoCommission() {
        return getDoubleProperty("default.commission.auto", 15.0);
    }
    
    /**
     * Muestra todas las propiedades en consola (para debugging)
     */
    public static void printAllProperties() {
        logger.info("=== Configuración actual del sistema ===");
        for (String key : properties.stringPropertyNames()) {
            logger.info(key + " = " + properties.getProperty(key));
        }
        logger.info("========================================");
    }
}
