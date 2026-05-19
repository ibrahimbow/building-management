package com.why.buildingmanagement.building.infrastructure.client.auth;

import com.why.buildingmanagement.building.application.port.out.LoadManagerInfoPort;
import com.why.buildingmanagement.building.application.result.ManagerInfoResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AuthServiceManagerClient implements LoadManagerInfoPort {

    private final RestClient authServiceRestClient;

    @Override
    public ManagerInfoResult loadManagerInfoById(final Long managerId) {
        return authServiceRestClient.get()
                .uri("/internal/users/{userId}", managerId)
                .retrieve()
                .body(ManagerInfoResult.class);
    }
}