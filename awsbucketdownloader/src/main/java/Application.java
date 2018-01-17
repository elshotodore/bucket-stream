import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Application {
    private static final String ROOT_DL_DIR = "/home/elshotodore/SharedWithHost/AWSDOWNLOADS/";

    public static void main(String[] args) {


        final AmazonS3 s3Client = AWSHelper.createS3Client();

        final List<String> bucketNames = FileHelper.readBucketListFromFile("../buckets_All_Unique.txt", true);
        int bucketLimit = 66;
        int fileLimit = 10;

        int bucketCounter = 0;
        int fileCounter;
        for (String bucketName : bucketNames) {
            System.out.println("Downloading from => " + bucketName);

            final List<URL> urlsFromBucket = AWSHelper.getUrlsFromBucket(s3Client, bucketName);
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
