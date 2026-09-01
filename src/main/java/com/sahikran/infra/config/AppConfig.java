package com.sahikran.infra.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private final Properties properties;
    private static volatile AppConfig appConfig = null;

    private AppConfig(){
        properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input); // Stream is consumed and closed here
            } else {
                System.err.println("Error: config.properties not found on classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppConfig getInstance(){
        if(appConfig == null){
            synchronized(AppConfig.class){
                if(appConfig == null){
                    appConfig = new AppConfig();
                }
            }
        }
        return appConfig;
    }

    // Global access point for all classes
    public String get(String key) {
        return properties.getProperty(key);
    }

    // Optional fallback helper
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
