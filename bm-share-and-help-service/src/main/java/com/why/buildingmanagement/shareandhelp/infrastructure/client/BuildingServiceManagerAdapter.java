package com.why.buildingmanagement.shareandhelp.infrastructure.client;

import com.why.buildingmanagement.shareandhelp.application.port.out.LoadManagerBuildingPort;
import com.why.buildingmanagement.shareandhelp.domain.exception.ManagerBuildingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingServiceManagerAdapter implements LoadManagerBuildingPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public UUID loadManagedBuildingIdByManagerUserId(final Long managerUserId) {

        final ManagerBuildingResponse response = restClientBuilder.build()
                                                                  .get()
                                                                  .uri(buildingServiceUrl + "/internal/managers/{managerUserId}/building", managerUserId)
                                                                  .retrieve()
                                                                  .body(ManagerBuildingResponse.class);

        if (response == null || response.buildingId() == null) {
            throw new ManagerBuildingNotFoundException(managerUserId);
        }
        return response.buildingId();
    }
}