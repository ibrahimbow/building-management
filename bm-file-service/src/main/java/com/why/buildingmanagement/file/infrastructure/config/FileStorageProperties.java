package com.why.buildingmanagement.file.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.storage")
public record FileStorageProperties(String uploadDir) {
}