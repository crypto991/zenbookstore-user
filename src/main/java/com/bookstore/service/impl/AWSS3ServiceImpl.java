package com.bookstore.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bookstore.service.AWSS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

    private final AmazonS3 amazonS3;

    public AWSS3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public void uploadFile(final MultipartFile multipartFile, Long bookId) {
        LOGGER.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(bucketName, file,bookId);
            LOGGER.info("File upload is completed.");
            file.delete();	// To remove the file locally created in the project folder.
        } catch (final AmazonServiceException ex) {
            LOGGER.info("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
        }
    }


    @Override
    public ByteArrayOutputStream downloadFile(String keyName) {
        try {
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos;
        } catch (IOException ioe) {
            LOGGER.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException ase) {
            LOGGER.info("sCaught an AmazonServiceException from GET requests, rejected reasons:");
            LOGGER.info("Error Message:    " + ase.getMessage());
            LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
            LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
            LOGGER.info("Error Type:       " + ase.getErrorType());
            LOGGER.info("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            LOGGER.info("Caught an AmazonClientException: ");
            LOGGER.info("Error Message: " + ace.getMessage());
            throw ace;
        }

        return null;
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
        }
        return file;
    }

    private void uploadFileToS3Bucket(final String bucketName, final File file, Long id) {
        final String uniqueFileName = id + ".png";
        LOGGER.info("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
    }

}

