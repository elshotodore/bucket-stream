import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                //.withForceGlobalBucketAccessEnabled(true)
                //.withRegion(Regions.US_WEST_2)
                .build();
        final List<String> buckets = Helper.readBucketList("../buckets.log", true);
        String bucketName = buckets.get(0);

        ObjectListing ol = s3.listObjects(bucketName);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            final String eTag = os.getETag();
            System.out.println(eTag);
            System.out.println(s3.getUrl(bucketName, os.getKey()));
        }
    }
}
