import com.amazonaws.services.s3.AmazonS3;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Application {
    private static final Properties properties = ConfigHelper.getProperties();
    private static final String ROOT_DL_DIR = properties.getProperty("download.root.folder");
    private static final String BUCKET_NAME_FILE = properties.getProperty("bucket.names.file");
    private static final String KEYWORDS_FILE = properties.getProperty("keyword.file");


    private static final AWSHelper awsHelper = new AWSHelper();

    private static int bucketLimit = 66;
    private static int fileLimit = 1;

    public static void main(String[] args) {

        final AmazonS3 s3Client = awsHelper.createS3Client();
        List<String> bucketNames = FileHelper.readBucketListFromFile(BUCKET_NAME_FILE, true);

        final List<String> interestingKeywords = FileHelper.readInterestingKeywords(KEYWORDS_FILE);

        int bucketCounter = 0;
        int fileCounter;
        //bucketNames = Arrays.asList("16552");
        for (String bucketName : bucketNames) {
            System.out.println("\nTrying bucket " + bucketName + " (" + (bucketCounter+1) + " of " + bucketLimit + ")");

            final List<URL> urlsFromBucket = awsHelper.recursivelyGetUrlsFromBucket(s3Client, bucketName);
            if(!urlsFromBucket.isEmpty()) {
                System.out.println(urlsFromBucket.size() +  " urls received.");

                final List<URL> filteredUrlsFromBucket = awsHelper.filterUrlsByKeyword(urlsFromBucket, interestingKeywords);
                System.out.println(filteredUrlsFromBucket.size() +  " urls left after filtering for keywords.");
                if(filteredUrlsFromBucket.size() > 0) {
                    System.out.println(ConfigHelper.ANSI_RED + "Yeah!" + ConfigHelper.ANSI_RESET);
                }
                //System.out.println("FILTERED URLS: \n" + filteredUrlsFromBucket);

    /*            fileCounter = 0;
                for (URL url : urlsFromBucket) {
                    File destination = new File(ROOT_DL_DIR + bucketName + url.getPath());
                    System.out.println("File =>" + url);
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
      */
            }
            if (++bucketCounter >= bucketLimit) {
                break;
            }
        }

        System.out.println("DONE.");
    }

}
