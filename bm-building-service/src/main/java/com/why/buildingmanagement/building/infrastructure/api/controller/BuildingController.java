package com.why.buildingmanagement.building.infrastructure.api.controller;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.CreateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.mapper.BuildingApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final CreateBuildingUseCase createBuildingUseCase;
    private final GetBuildingByCodeUseCase getBuildingByCodeUseCase;
    private final JoinBuildingUseCase joinBuildingUseCase;
    private final BuildingApiMapper mapper;

    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(@Valid @RequestBody final CreateBuildingRequest request) {

        final CreateBuildingCommand command = new CreateBuildingCommand(
                request.buildingName(),
                request.address(),
                request.managerName(),
                request.managerEmail(),
                request.totalApartments(),
                request.emergencyPhone());

        final BuildingInfoResult result = createBuildingUseCase.createBuilding(command);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + result.id()))
                .body(mapper.toResponse(result));
    }


    @GetMapping("/code/{code}")
    public ResponseEntity<BuildingInfoResult> getBuildingByCode(@PathVariable("code") final String code) {
        final BuildingInfoResult result = getBuildingByCodeUseCase.getBuildingByCode(code);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/join")
    public ResponseEntity<BuildingInfoResult> joinBuilding(@Valid @RequestBody final JoinBuildingRequest request) {

        final JoinBuildingCommand command = new JoinBuildingCommand(
                request.code(),
                request.tenantUserId(),
                request.tenantEmail());

        final BuildingInfoResult result = joinBuildingUseCase.joinBuilding(command);

        return ResponseEntity.ok(result);
    }
}
