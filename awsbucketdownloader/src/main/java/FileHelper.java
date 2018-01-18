import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

    /***
     * Create a directory for the download (if it does not exist).
     * @param directoryName
     */
    public static void createDirectory(String directoryName) {
        Path path = Paths.get(directoryName);
        try {
            if(!Files.exists(path)) {
                System.out.printf("Creating directory " + directoryName);
                Files.createDirectories(path);
            }
            else {
                System.out.println("Directory " + directoryName + " already exists. Skipping...");
            }
        } catch (IOException ioe) {
            System.out.println("ERROR: Cannot create directory " + directoryName);
            System.out.printf("Exception was: " + ioe.getMessage());
        }
    }

    /***
     * Read the bucket list from file. bucketNamesOnly returns just the names, otherwise the complete entry ([BUCKET_NAME].s3.amazonaws.com).
     * @param fileName
     * @param bucketNamesOnly
     * @return
     */
    public static List<String> readBucketListFromFile(String fileName, boolean bucketNamesOnly) {
        List<String> buckets = new ArrayList();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            if (bucketNamesOnly) {
                buckets = stream.map(b -> b.substring(0, b.indexOf("."))).collect(Collectors.toList());
            } else {
                buckets = stream.collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buckets;
    }

    /***
     * Read the interesting keywords list from file.
     * @param fileName
     * @return
     */
    public static List<String> readInterestingKeywords(String fileName) {
        List<String> keywords = new ArrayList();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            keywords = stream.map(kw -> kw.toLowerCase()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keywords;
    }
}
