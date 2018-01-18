import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class Application {
    private static final Properties properties = ConfigHelper.getProperties();
    private static final String ROOT_DL_DIR = properties.getProperty("download.root.folder");
    private static final String BUCKET_NAME_FILE = properties.getProperty("bucket.names.file");
    private static final String KEYWORDS_FILE = properties.getProperty("keyword.file");


    private static final AWSHelper awsHelper = new AWSHelper();

    private static int bucketLimit = 66;
    private static int fileLimit = 10;

    public static void main(String[] args) {

        final AmazonS3 s3Client = awsHelper.createS3Client();
        final List<String> bucketNames = FileHelper.readBucketListFromFile(BUCKET_NAME_FILE, true);

        final List<String> interestingKeywords = FileHelper.readInterestingKeywords(KEYWORDS_FILE);
        System.out.println(interestingKeywords);
        System.exit(13);

        int bucketCounter = 0;
        int fileCounter;

        for (String bucketName : bucketNames) {
            System.out.println("Downloading from => " + bucketName);

            final List<URL> urlsFromBucket = awsHelper.getUrlsFromBucket(s3Client, bucketName);
            //System.out.println(urlsFromBucket);
//System.exit(13);
            fileCounter = 0;
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
            if(++bucketCounter > bucketLimit) break;
        }
        System.out.println("DONE.");
    }
}
