import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AWSHelper {
    /***
     * Create the client.
     * @return s3Client
     */
    public AmazonS3 createS3Client() {
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
    public List<URL> recursivelyGetUrlsFromBucket(AmazonS3 s3Client, String bucketName) {
        ObjectListing urlListing;
        List<S3ObjectSummary> s3ObjectSummaries;
        final List<URL> urls = new ArrayList<>();
        try {
            urlListing = s3Client.listObjects(bucketName);
            s3ObjectSummaries = urlListing.getObjectSummaries();

            for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
                URL url = s3Client.getUrl(bucketName, s3ObjectSummary.getKey());
                if (url != null) {
                    urls.add(removeRegionFromUrl(url));
                }
            }

            while (urlListing.isTruncated()) {
                urlListing = s3Client.listNextBatchOfObjects(urlListing);
                s3ObjectSummaries = urlListing.getObjectSummaries();
                for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
                    URL url = s3Client.getUrl(bucketName, s3ObjectSummary.getKey());
                    if (url != null) {
                        urls.add(removeRegionFromUrl(url));
                    }
                }
            }
        }
        catch (AmazonS3Exception as3e) {
            System.out.printf("ERROR: Status code: " + as3e.getStatusCode() + ", Error code: " + as3e.getErrorCode());
        }
        return urls;
    }

    private URL removeRegionFromUrl(URL url)  {
        try {
            return new URL(url.toString().replaceAll("s3\\.(.*)\\.amazonaws", "s3.amazonaws"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<URL> filterUrlsByKeyword(List<URL> urlList, List<String> keywordList) {
        List<URL> filterdUrlList = new ArrayList<>();
        for (URL url : urlList) {
            for (String keyword : keywordList) {
                if (url.toString().contains(keyword)) {
                    filterdUrlList.add(url);
                }
            }
        }

        return filterdUrlList;
    }
}
