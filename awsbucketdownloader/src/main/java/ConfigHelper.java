import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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
