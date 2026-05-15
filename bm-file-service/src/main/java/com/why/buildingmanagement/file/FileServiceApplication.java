package com.why.buildingmanagement.file;

import com.why.buildingmanagement.file.infrastructure.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}