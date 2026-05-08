package com.why.buildingmanagement.building.infrastructure.api.controller.manager;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.CreateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.UpdateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.mapper.BuildingApiMapper;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class ManagerBuildingController {

    private final CreateBuildingUseCase createBuildingUseCase;
    private final GetBuildingByCodeUseCase getBuildingByCodeUseCase;
    private final JoinBuildingUseCase joinBuildingUseCase;
    private final GetMyBuildingsUseCase getMyBuildingsUseCase;
    private final GetMyBuildingByIdUseCase getMyBuildingByIdUseCase;
    private final UpdateMyBuildingUseCase updateMyBuildingUseCase;
    private final DeleteMyBuildingUseCase deleteMyBuildingUseCase;

    private final BuildingApiMapper mapper;
    private final CurrentUserService currentUserService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(
            @Valid @RequestBody final CreateBuildingRequest request) {
        final var current = currentUserService.getCurrentUser();

        final CreateBuildingCommand command = new CreateBuildingCommand(
                request.buildingName(),
                request.address(),
                current.userId(),
                request.totalApartments(),
                request.emergencyPhone());

        final BuildingInfoResult result = createBuildingUseCase.createBuilding(command);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + result.id()))
                .body(mapper.toResponse(result));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    @PostMapping("/join")
    public ResponseEntity<BuildingResponse> joinBuilding(
            @Valid @RequestBody final JoinBuildingRequest request) {
        final var current = currentUserService.getCurrentUser();

        final JoinBuildingCommand command = new JoinBuildingCommand(
                request.code(),
                current.userId(),
                current.email());

        final BuildingInfoResult result = joinBuildingUseCase.joinBuilding(command);

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    @GetMapping("/code/{code}")
    public ResponseEntity<BuildingResponse> getBuildingByCode(
            @PathVariable("code") final String code) {
        final BuildingInfoResult result = getBuildingByCodeUseCase.getBuildingByCode(code);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/managed")
    public ResponseEntity<List<BuildingResponse>> getMyBuildings() {
        final var current = currentUserService.getCurrentUser();

        final List<BuildingResponse> response = getMyBuildingsUseCase
                .getMyBuildings(current.userId())
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponse> getMyBuildingById(
            @PathVariable("id") final UUID id) {
        final var current = currentUserService.getCurrentUser();

        final BuildingInfoResult result =
                getMyBuildingByIdUseCase.getMyBuildingById(id, current.userId());

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponse> updateMyBuilding(
            @PathVariable("id") final UUID id,
            @Valid @RequestBody final UpdateBuildingRequest request) {
        final var current = currentUserService.getCurrentUser();

        final UpdateMyBuildingCommand command = new UpdateMyBuildingCommand(
                id,
                current.userId(),
                request.buildingName(),
                request.address(),
                request.totalApartments(),
                request.emergencyPhone());

        final BuildingInfoResult result = updateMyBuildingUseCase.updateMyBuilding(command);

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyBuilding(
            @PathVariable("id") final UUID id) {
        final var current = currentUserService.getCurrentUser();

        final DeleteMyBuildingCommand command =
                new DeleteMyBuildingCommand(id, current.userId());

        deleteMyBuildingUseCase.deleteMyBuilding(command);

        return ResponseEntity.noContent().build();
    }
}