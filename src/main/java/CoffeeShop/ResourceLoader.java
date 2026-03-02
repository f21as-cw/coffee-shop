package CoffeeShop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceLoader {

    public static Properties loadConfig() {
        Properties props = new Properties();

        try (InputStream is = ResourceLoader.class.getResourceAsStream("/config.properties")) {

            if (is == null) {
                throw new IOException("Config file 'config.properties' not found in resources!");
            }

            props.load(is);

        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }

        return props;
    }

}
