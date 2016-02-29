package modules.storage;

import akka.dispatch.Futures;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.inject.Inject;
import play.Configuration;
import play.Logger;
import play.libs.F;
import play.mvc.Result;
import scala.concurrent.Promise;
import views.html.error;

import java.net.URL;
import java.nio.file.Path;

import static play.mvc.Results.internalServerError;
import static play.mvc.Results.redirect;

public class AmazonS3Storage implements Storage {
    private static final Logger.ALogger logger = Logger.of(AmazonS3Storage.class);

    private final AWSCredentials credentials;
    private final String bucketName;

    @Inject
    public AmazonS3Storage (Configuration configuration) {
        bucketName = configuration.getString("storage.s3.bucket", "thunderbit");

        String accessKey = configuration.getString("storage.s3.accesskey");
        String secretKey = configuration.getString("storage.s3.secretkey");
        credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 amazonS3 = new AmazonS3Client(credentials);

        if (configuration.getBoolean("storage.s3.createBucket", true)) {
            try {
                if (!(amazonS3.doesBucketExist(bucketName))) {
                    amazonS3.createBucket(new CreateBucketRequest(bucketName));
                }

                String bucketLocation = amazonS3.getBucketLocation(new GetBucketLocationRequest(bucketName));
                logger.info("Amazon S3 bucket created at " + bucketLocation);
            } catch (AmazonServiceException ase) {
                logAmazonServiceException (ase);
            } catch (AmazonClientException ace) {
                logAmazonClientException(ace);
            }
        }
    }

    @Override
    public F.Promise<Void> store(Path path, String key, String name) {
        Promise<Void> promise = Futures.promise();

        TransferManager transferManager = new TransferManager(credentials);
        try {
            Upload upload = transferManager.upload(bucketName, key, path.toFile());
            upload.addProgressListener((ProgressListener) progressEvent -> {
                if (progressEvent.getEventType().isTransferEvent()) {
                    if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
                        transferManager.shutdownNow();
                        promise.success(null);
                    } else if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_FAILED_EVENT)) {
                        transferManager.shutdownNow();
                        logger.error(progressEvent.toString());
                        promise.failure(new Exception(progressEvent.toString()));
                    }
                }
            });
        } catch (AmazonServiceException ase) {
            logAmazonServiceException (ase);
        } catch (AmazonClientException ace) {
            logAmazonClientException(ace);
        }

        return F.Promise.wrap(promise.future());
    }

    @Override
    public F.Promise<Result> getDownload(String key, String name) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        responseHeaders.setContentDisposition("attachment; filename="+name);
        generatePresignedUrlRequest.setResponseHeaders(responseHeaders);

        AmazonS3 amazonS3 = new AmazonS3Client(credentials);

        try {
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            return F.Promise.pure(redirect(url.toString()));
        } catch (AmazonClientException ace) {
            logAmazonClientException (ace);
            return F.Promise.pure(internalServerError(error.render()));
        }
    }

    @Override
    public F.Promise<Void> delete(String key, String name) {
        Promise<Void> promise = Futures.promise();

        AmazonS3 amazonS3 = new AmazonS3Client(credentials);
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, key);
        request.withGeneralProgressListener(progressEvent -> {
            if (progressEvent.getEventType().isTransferEvent()) {
                if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
                    promise.success(null);
                } else if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_FAILED_EVENT)) {
                    logger.error(progressEvent.toString());
                    promise.failure(new Exception(progressEvent.toString()));
                }
            }
        });

        try {
            amazonS3.deleteObject(request);
        } catch (AmazonServiceException ase) {
            logAmazonServiceException (ase);
        } catch (AmazonClientException ace) {
            logAmazonClientException(ace);
        }

        return F.Promise.wrap(promise.future());
    }

    private void logAmazonServiceException (AmazonServiceException ase) {
        logger.error("Caught an AmazonServiceException, which " +
                "means your request made it " +
                "to Amazon S3, but was rejected with an error response " +
                "for some reason." +
                " Error Message: " + ase.getMessage() +
                " HTTP Status Code: " + ase.getStatusCode() +
                " AWS Error Code: " + ase.getErrorCode() +
                " Error Type: " + ase.getErrorType() +
                " Request ID: " + ase.getRequestId(), ase);
    }

    private void logAmazonClientException (AmazonClientException ace) {
        logger.error("Caught an AmazonClientException, which " +
                "means the client encountered " +
                "an internal error while trying to " +
                "communicate with S3, " +
                "such as not being able to access the network." +
                " Error Message: " + ace.getMessage(), ace);
    }
}
