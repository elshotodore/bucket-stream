import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    private static final String RESOURCE_FILE_NAME = "application.properties";

    public static Properties getProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(RESOURCE_FILE_NAME)) {
            properties.load(resourceStream);
        } catch (Exception e)

        {
            System.out.println("ERROR: Unable to read property file " + RESOURCE_FILE_NAME);
        }
        return properties;
    }
}
