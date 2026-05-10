package com.why.buildingmanagement.announcement.infrastructure.client;

import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingAccessClient implements BuildingAccessPort {

    private final RestTemplate restTemplate;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public UUID getManagerBuildingId(final Long managerId) {
        final BuildingAccessResponse response = restTemplate.getForObject(
                buildingServiceUrl + "/internal/managers/" + managerId + "/building",
                BuildingAccessResponse.class);

        if (response == null) {
            throw new IllegalStateException("Manager has no building");
        }

        return response.buildingId();
    }

    @Override
    public UUID getTenantActiveBuildingId(final Long tenantUserId) {
        final BuildingAccessResponse response = restTemplate.getForObject(
                buildingServiceUrl + "/internal/tenants/" + tenantUserId + "/active-building",
                BuildingAccessResponse.class);

        if (response == null) {
            throw new IllegalStateException("Tenant has no active building");
        }

        return response.buildingId();
    }
}