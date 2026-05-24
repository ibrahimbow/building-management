package com.why.buildingmanagement.chat.infrastructure.client;

import com.why.buildingmanagement.chat.application.port.out.LoadManagerBuildingPort;
import com.why.buildingmanagement.chat.domain.exception.ManagerBuildingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingServiceManagerBuildingAdapter implements LoadManagerBuildingPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.building-service.url}")
    private String buildingServiceUrl;

    @Override
    public UUID loadBuildingIdByManagerUserId(final Long managerUserId) {

        final ManagerBuildingResponse response = restClientBuilder.build()
                        .get()
                        .uri(buildingServiceUrl
                                        + "/internal/managers/"
                                        + managerUserId
                                        + "/building")
                        .retrieve()
                        .body(ManagerBuildingResponse.class);

        if (response == null) {
            throw new ManagerBuildingNotFoundException(managerUserId);
        }

        return response.buildingId();
    }
}