package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.why.buildingmanagement.shareandhelp.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.TenantBuildingResponse;
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
                .uri(buildingServiceUrl + "/internal/tenants/{tenantUserId}/building", tenantUserId)
                .retrieve()
                .body(TenantBuildingResponse.class);

        if (response == null || response.buildingId() == null) {
            throw new IllegalStateException("Tenant has no active building.");
        }

        return response.buildingId();
    }
}