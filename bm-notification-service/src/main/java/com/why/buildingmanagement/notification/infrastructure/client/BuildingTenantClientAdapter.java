package com.why.buildingmanagement.notification.infrastructure.client;

import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingTenantClientAdapter implements LoadBuildingTenantUsersPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public List<Long> loadTenantUserIds(final UUID buildingId) {

        final List<BuildingTenantResponse> response = restClientBuilder.build()
                        .get()
                        .uri(buildingServiceUrl
                                        + "/internal/buildings/"
                                        + buildingId
                                        + "/active-tenants")
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

        if (response == null) {
            return List.of();
        }

        return response.stream()
                        .map(BuildingTenantResponse::userId)
                        .toList();
    }
}