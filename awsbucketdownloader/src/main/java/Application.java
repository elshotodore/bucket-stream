import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Application {
    private static final Properties properties = ConfigHelper.getProperties();
    private static final String ROOT_DL_DIR = properties.getProperty("download.root.folder");
    private static final String BUCKET_NAME_FILE = properties.getProperty("bucket.names.file");
    private static final String KEYWORDS_FILE = properties.getProperty("keyword.file");


    private static final AWSHelper awsHelper = new AWSHelper();

    private static int bucketLimit = 10;
    private static int fileLimit = 1;

    public static void main(String[] args) {
        final AmazonS3 s3Client = awsHelper.createS3Client();
        List<String> bucketNames = FileHelper.readBucketListFromFile(BUCKET_NAME_FILE, true);
        final List<String> interestingKeywords = FileHelper.readInterestingKeywords(KEYWORDS_FILE);
        List<URL> interestingUrls = new ArrayList<>();
        int bucketCounter = 0;
        int fileCounter;
        bucketNames = Arrays.asList("ferc");
        for (String bucketName : bucketNames) {
            System.out.println("\nTrying bucket " + bucketName + " (" + (bucketCounter + 1) + " of " + bucketLimit + ")");

            final List<URL> urlsFromBucket = awsHelper.recursivelyGetUrlsFromBucket(s3Client, bucketName);
            if (!urlsFromBucket.isEmpty()) {
                System.out.println(urlsFromBucket.size() + " urls received.");

                final List<URL> filteredUrlsFromBucket = awsHelper.filterUrlsByKeyword(urlsFromBucket, interestingKeywords);
                System.out.println(filteredUrlsFromBucket.size() + " urls left after filtering for keywords.");
                if (filteredUrlsFromBucket.size() > 0) {
                    System.out.println(ConfigHelper.ANSI_RED + "Yeah!" + ConfigHelper.ANSI_RESET);
                }
                // to save later
                interestingUrls.addAll(filteredUrlsFromBucket);
                //System.out.println("FILTERED URLS: \n" + filteredUrlsFromBucket);

                fileCounter = 0;
                for (URL url : filteredUrlsFromBucket) {
                    File destination = new File(ROOT_DL_DIR + bucketName + url.getPath());
                    System.out.println("Downloading..." + url);
                    destination.getParentFile().mkdirs();
                    if(!url.toString().endsWith("/")) {
                        try {
                            FileUtils.copyURLToFile(url, destination);
                        }
                        catch (IOException ioe) {
                            System.out.println("Could not download file " + url.toString());
                        }
                    }
                    if(++fileCounter  > fileLimit) break;
                }
            }
            if (++bucketCounter >= bucketLimit) {
                break;
            }
        }
        FileHelper.writeInterestingUrlsToFile("interestingurls.txt", interestingUrls);
        System.out.println("DONE.");
    }

}
