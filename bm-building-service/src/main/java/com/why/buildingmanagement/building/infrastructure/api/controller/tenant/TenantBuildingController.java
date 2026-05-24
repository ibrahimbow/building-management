package com.why.buildingmanagement.building.infrastructure.api.controller.tenant;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.mapper.BuildingApiMapper;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tenant/buildings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TENANT')")
public class TenantBuildingController {

    private final JoinBuildingUseCase joinBuildingUseCase;
    private final GetBuildingByCodeUseCase getBuildingByCodeUseCase;
    private final GetMyBuildingUseCase getMyBuildingUseCase;
    private final LeaveBuildingUseCase leaveBuildingUseCase;
    private final BuildingApiMapper mapper;
    private final CurrentUserService currentUserService;

    @PostMapping("/join")
    public ResponseEntity<BuildingResponse> joinBuilding(@Valid @RequestBody final JoinBuildingRequest request) {

        final var current = currentUserService.getCurrentUser();
        final BuildingInfoResult result = joinBuildingUseCase.joinBuilding(
                new JoinBuildingCommand(
                        request.code(),
                        current.userId(),
                        current.displayName(),
                        current.email(),
                        current.phoneNumber()));

        return ResponseEntity
                .created(URI.create("/api/tenant/buildings/my-building"))
                .body(mapper.toResponse(result));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BuildingResponse> getBuildingByCode(@PathVariable("code") final String code) {

        final BuildingInfoResult result = getBuildingByCodeUseCase.getBuildingByCode(code);

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/my-building")
    public ResponseEntity<BuildingResponse> getMyBuilding() {
        final var current = currentUserService.getCurrentUser();
        final BuildingInfoResult result = getMyBuildingUseCase.getMyBuilding(current.userId());

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/my-building/leave")
    public ResponseEntity<Void> leaveBuilding() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        leaveBuildingUseCase.leaveBuilding(new LeaveBuildingCommand(currentUser.userId()));

        return ResponseEntity.noContent().build();
    }
}
