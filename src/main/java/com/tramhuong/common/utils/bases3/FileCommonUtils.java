package com.tramhuong.common.utils.bases3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.tramhuong.common.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class FileCommonUtils {

    private String endpointUrl;
    private String bucketName;
    private String accessKey;
    private String secretKey;
    private String localFolder;
    private String imageProductFolder;

    private String urlDowloadFile;

    private static FileCommonUtils instance;

    private AmazonS3 s3Client;
    private FileCommonUtils(){}

    public static FileCommonUtils getInstance(){
        if(instance == null){
            synchronized(FileCommonUtils.class) {
                instance = new FileCommonUtils();
            }
        }
        return instance;
    }
    public void loadConfig(
            String endpointUrl,
            String secretKey,
            String accessKey,
            String bucketName,
            String urlDowloadFile) {
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        this.bucketName = bucketName;
        this.endpointUrl = endpointUrl;
        this.urlDowloadFile = urlDowloadFile;
        s3Client = createS3();
    }

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3Client = new AmazonS3Client(credentials);
    }
    public AmazonS3 createS3() {
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return null;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return null;
                    }
                }))
                .build();
        return amazonS3;
    }

    public static File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        FileInputStream fileInputStream = new FileInputStream(multipartFile.getOriginalFilename());
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    public String generateFileName(MultipartFile multipartFile) {
        return new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
    }

    public String generateFileName(String fileName) {
        return new Date().getTime() + "-" + fileName.replace(" ", "_");
    }

    public String generateUrl(String fileName) {
        return urlDowloadFile + "/" + fileName;
    }

    public String saveFileToLocal(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            File fileLocal = new File(CommonConstant.PATH_LOCAL_SAVE_IMAGE + File.separator + UUID.randomUUID() + fileName);
            multipartFile.transferTo(fileLocal);
            log.info("save success");
            return fileLocal.getPath();
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    public String uploadFileToS3(MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileToS3(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    public static String uploadFileToS3(String pathFile) {
        String fileUrl = "";
        try {
            FileOutputStream outputStream = new FileOutputStream(pathFile);
            File file = new File(pathFile);
//            File file = convertMultiPartToFile(multipartFile);
//            String fileName = generateFileName(multipartFile);
//            fileUrl = s3StorageConfig.getEndpointUrl() + "/" + s3StorageConfig.getBucketName() + "/" + fileName;
//            storageService.uploadFileToS3(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    public void uploadFileToS3(String fileName, File file) {
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public byte[] getFileFromS3(String filePath) {
        try {
            long timeMillis =  Calendar.getInstance().getTimeInMillis();
            String localPath = "s3StorageConfig.getLocalFolder()" + File.separator + Thread.currentThread().getId() + File.separator + timeMillis;
            File localPathFile = new File(localPath);
            localPathFile.getParentFile().mkdirs();
            log.info("Downloading an object with key={} to={}", filePath, localPath);

            final S3Object s3Object = s3Client.getObject(bucketName, filePath);
            try (
                    S3ObjectInputStream inputStream = s3Object.getObjectContent();
                    FileOutputStream outputStream = new FileOutputStream(localPath);
            ) {
                IOUtils.copy(inputStream, outputStream);
                log.info("File downloaded successfully.");
            } catch (final IOException ex) {
                log.warn("IO Error Message={}", ex.getMessage());
            }
            byte[] byteArray = FileUtils.readFileToByteArray(localPathFile);
            Files.delete(localPathFile.toPath());
            return byteArray;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
