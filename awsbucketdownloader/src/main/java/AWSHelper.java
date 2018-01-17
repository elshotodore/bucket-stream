import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AWSHelper {
    /***
     * Create the client.
     * @return s3Client
     */
    public static AmazonS3 createS3Client() {
        final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withForceGlobalBucketAccessEnabled(true)
                .build();
        return s3Client;
    }

    /***
     * Get the list of files (URLs) for given bucketName
     * @param s3Client
     * @param bucketName
     * @return List of URLs
     */
    public static List<URL> getUrlsFromBucket(AmazonS3 s3Client, String bucketName) {
        final List<URL> urls = new ArrayList<>();
        try {
            final ObjectListing ol = s3Client.listObjects(bucketName);
            final List<S3ObjectSummary> s3ObjectSummaries = ol.getObjectSummaries();

            // remove the region from url
            for (S3ObjectSummary s3ObjectSummary: s3ObjectSummaries) {
                String urlString = s3Client.getUrl(bucketName, s3ObjectSummary.getKey()).toString();
                urlString = urlString.replaceAll("s3\\.(.*)\\.amazonaws", "s3.amazonaws");
                try {
                    URL url = new URL(urlString);
                    urls.add(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        catch(AmazonS3Exception as3e) {
            System.out.printf("AmazonS3Exception: " + as3e);
        }
        return urls;
    }
}
