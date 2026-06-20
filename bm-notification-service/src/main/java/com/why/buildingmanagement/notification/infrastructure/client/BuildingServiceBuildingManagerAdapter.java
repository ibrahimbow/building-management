package com.why.buildingmanagement.notification.infrastructure.client;

import com.why.buildingmanagement.notification.application.port.out.LoadBuildingManagerUserPort;
import com.why.buildingmanagement.notification.infrastructure.exception.BuildingManagerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingServiceBuildingManagerAdapter implements LoadBuildingManagerUserPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public Long loadManagerUserIdByBuildingId(final UUID buildingId) {

        final BuildingManagerResponse response = restClientBuilder.build()
                                                                  .get()
                                                                  .uri(buildingServiceUrl
                                                                                       + "/internal/buildings/"
                                                                                       + buildingId
                                                                                       + "/manager")
                                                                  .retrieve()
                                                                  .body(BuildingManagerResponse.class);

        if (response == null) {
            throw new BuildingManagerNotFoundException(buildingId);
        }

        return response.managerUserId();
    }
}