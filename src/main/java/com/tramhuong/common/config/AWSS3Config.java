package com.tramhuong.common.config;

import com.amazonaws.services.s3.AmazonS3;
import com.tramhuong.common.utils.bases3.FileCommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSS3Config {

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("C:\\Users\\HLC_2021\\Documents\\TramHuongBE\\S3Storage")
    private String localFolder;

    @Value("image")
    private String imageProductFolder;

    @Value("https://app-tram-huong.s3.ap-southeast-1.amazonaws.com")
    private String urlDowloadFile;




    @Bean
    public FileCommonUtils fileCommonUtils() {
        FileCommonUtils.getInstance().loadConfig(
                endpointUrl,
                secretKey,
                accessKey,
                bucketName,
                urlDowloadFile

        );
        return FileCommonUtils.getInstance();
    }
}
