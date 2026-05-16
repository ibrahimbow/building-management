package com.why.buildingmanagement.chat.infrastructure.persistence.client;

import com.why.buildingmanagement.chat.application.port.out.LoadTenantBuildingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingServiceTenantBuildingAdapter implements LoadTenantBuildingPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public UUID loadActiveBuildingIdByTenantUserId(final Long tenantUserId) {

        final TenantBuildingResponse response = restClientBuilder.build()
                .get()
                .uri(buildingServiceUrl
                        + "/internal/tenants/"
                        + tenantUserId
                        + "/building")
                .retrieve()
                .body(TenantBuildingResponse.class);

        return response.buildingId();
    }
}