import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;
import java.util.stream.Collectors;

public class Application {
    private static final String ROOT_DL_DIR = "DOWNLOADS/";
    public static void main(String[] args) {

        Helper.createDirectory(ROOT_DL_DIR + "BLA");
        System.exit(13);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withForceGlobalBucketAccessEnabled(true)
                //.withRegion(Regions.US_WEST_2)
                .build();

        final List<String> buckets = Helper.readBucketList("../buckets.log", true);
        String bucketName = "pilot"; //buckets.get(0);
        System.out.printf("bucket name => " + bucketName);
        /*
        String  region = s3.getBucketLocation(bucketName);
        System.out.printf("region => " + region);
        */
        ObjectListing ol = s3.listObjects(bucketName);


        List<S3ObjectSummary> s3ObjectSummaries = ol.getObjectSummaries();
        final List<String> cleanedUrls = s3ObjectSummaries.stream().map(o -> s3.getUrl(bucketName, o.getKey()).toString()).map(o -> o.replaceAll("s3\\.(.*)\\.amazonaws", "s3.amazonaws")).collect(Collectors.toList());
        System.out.println(cleanedUrls);
        /*
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            URL s3UrlWithRegion = s3.getUrl(bucketName, s3ObjectSummary.getKey());
            final String urlString = s3UrlWithRegion.toString().replaceAll("s3\\.(.*)\\.amazonaws", "s3.amazonaws");
            System.out.println(urlString);
        }
        */
    }
}
