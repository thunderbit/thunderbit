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
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.inject.Inject;
import play.Configuration;
import play.Logger;
import play.libs.F;
import scala.concurrent.Promise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AmazonS3Storage implements Storage {
    private final AWSCredentials credentials;
    private final String bucketName;

    @Inject
    public AmazonS3Storage (Configuration configuration) {
        bucketName = configuration.getString("storage.s3.bucketName");

        String accessKey = configuration.getString("storage.s3.accesskey");
        String secretKey = configuration.getString("storage.s3.secretkey");
        credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 amazonS3 = new AmazonS3Client(credentials);

        try {
            if(!(amazonS3.doesBucketExist(bucketName))) {
                amazonS3.createBucket(new CreateBucketRequest(bucketName));
            }

            String bucketLocation = amazonS3.getBucketLocation(new GetBucketLocationRequest(bucketName));
            Logger.info("Amazon S3 bucket created at " + bucketLocation);
        } catch (AmazonServiceException ase) {
            Logger.error("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason." +
                    " Error Message: " + ase.getMessage() +
                    " HTTP Status Code: " + ase.getStatusCode() +
                    " AWS Error Code: " + ase.getErrorCode() +
                    " Error Type: " + ase.getErrorType() +
                    " Request ID: " + ase.getRequestId()
            );
        } catch (AmazonClientException ace) {
            Logger.error("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network." +
                    " Error Message: " + ace.getMessage()
            );
        }

    }

    @Override
    public F.Promise<Void> store(Path path, String key) {
        Promise<Void> promise = Futures.promise();

        TransferManager transferManager = new TransferManager(credentials);
        Upload upload = transferManager.upload(bucketName, key, path.toFile());

        upload.addProgressListener((ProgressListener) progressEvent -> {
            if (progressEvent.getEventType().isTransferEvent()) {
                if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
                    transferManager.shutdownNow();
                    promise.success(null);
                } else if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_FAILED_EVENT)) {
                    transferManager.shutdownNow();
                    promise.failure(new Exception("Upload failed"));
                }
            }
        });

        return F.Promise.wrap(promise.future());
    }

    @Override
    public F.Promise<Path> retrieve(String key) {
        Promise<Path> promise = Futures.promise();

        TransferManager transferManager = new TransferManager(credentials);

        try {
            Path path = Files.createTempFile(key, "");

            Download download = transferManager.download(bucketName, key, path.toFile());

            download.addProgressListener((ProgressListener) progressEvent -> {
                if (progressEvent.getEventType().isTransferEvent()) {
                    if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
                        transferManager.shutdownNow();
                        promise.success(path);
                    } else if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_FAILED_EVENT)) {
                        transferManager.shutdownNow();
                        promise.failure(new Exception("Download failed"));
                    }
                }
            });
        } catch (IOException e) {
            promise.failure(e);
        }

        return F.Promise.wrap(promise.future());
    }

    @Override
    public F.Promise<Void> delete(String key) {
        Promise<Void> promise = Futures.promise();

        AmazonS3 amazonS3 = new AmazonS3Client(credentials);
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, key);
        request.withGeneralProgressListener(progressEvent -> {
            if (progressEvent.getEventType().isTransferEvent()) {
                if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
                    promise.success(null);
                } else if (progressEvent.getEventType().equals(ProgressEventType.TRANSFER_FAILED_EVENT)) {
                    promise.failure(new Exception("Delete failed"));
                }
            }
        });
        amazonS3.deleteObject(request);

        return F.Promise.wrap(promise.future());
    }
}
