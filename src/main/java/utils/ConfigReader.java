package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("⚠️ Failed to load config.properties: src/resources/config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
