package com.tramhuong.common.utils.bases3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;

@Slf4j
public class StorageServiceImpl {
//
//    private AmazonS3 s3Client;
//
//    private S3StorageConfig s3StorageConfig;
//
//    public void uploadFileToS3(String fileName, File file) {
//        s3Client.putObject(new PutObjectRequest(s3StorageConfig.getBucketName(), fileName, file)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
//    }
//
//    public byte[] getFileFromS3(String filePath) {
//        try {
//            long timeMillis =  Calendar.getInstance().getTimeInMillis();
//            String localPath = s3StorageConfig.getLocalFolder() + File.separator + Thread.currentThread().getId() + File.separator + timeMillis;
//            File localPathFile = new File(localPath);
//            localPathFile.getParentFile().mkdirs();
//            log.info("Downloading an object with key={} to={}", filePath, localPath);
//
//            final S3Object s3Object = s3Client.getObject(s3StorageConfig.getBucketName(), filePath);
//            try (
//                    S3ObjectInputStream inputStream = s3Object.getObjectContent();
//                    FileOutputStream outputStream = new FileOutputStream(localPath);
//            ) {
//                IOUtils.copy(inputStream, outputStream);
//                log.info("File downloaded successfully.");
//            } catch (final IOException ex) {
//                log.warn("IO Error Message={}", ex.getMessage());
//            }
//            byte[] byteArray = FileUtils.readFileToByteArray(localPathFile);
//            Files.delete(localPathFile.toPath());
//            return byteArray;
//        } catch (IOException ex) {
//            log.error(ex.getMessage());
//            throw new RuntimeException(ex);
//        }
//    }
}
