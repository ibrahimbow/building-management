package com.why.buildingmanagement.notification.infrastructure.client;

import com.why.buildingmanagement.notification.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.notification.infrastructure.exception.TenantBuildingNotFoundException;
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

        if (response == null) {
            throw new TenantBuildingNotFoundException(tenantUserId);
        }

        return response.buildingId();
    }
}