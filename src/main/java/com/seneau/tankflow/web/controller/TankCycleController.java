package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import com.seneau.tankflow.web.dto.request.CreateTankCycleRequest;
import com.seneau.tankflow.web.dto.response.StepDurationDetail;
import com.seneau.tankflow.web.dto.response.TankCycleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/cycles")
@RequiredArgsConstructor
public class TankCycleController {

    private final TankCycleService tankCycleService;

    @PostMapping
    public ResponseEntity<TankCycleResponse> createCycle(
            @Valid @RequestBody CreateTankCycleRequest request) {
        log.info("Creating cycle for asset: {}", request.getAssetId());

        TankCycle cycle = tankCycleService.createCycle(
                request.getAssetId(),
                request.getStartedAt(),
                request.getDeadlineAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cycle));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankCycleResponse> getCycleById(@PathVariable Long id) {
        log.info("Fetching cycle: {}", id);

        TankCycle cycle = tankCycleService.getCycleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cycle not found with ID: " + id));

        return ResponseEntity.ok(toResponse(cycle));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<TankCycleResponse>> getCyclesByAssetId(@PathVariable Long assetId) {
        log.info("Fetching cycles for asset: {}", assetId);

        List<TankCycle> cycles = tankCycleService.getCyclesByAssetId(assetId);
        List<TankCycleResponse> responses = cycles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TankCycleResponse>> getCyclesByStatus(@PathVariable String status) {
        log.info("Fetching cycles with status: {}", status);

        CycleStatus cycleStatus = CycleStatus.valueOf(status);
        List<TankCycle> cycles = tankCycleService.getCyclesByStatus(cycleStatus);
        List<TankCycleResponse> responses = cycles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TankCycleResponse> updateCycleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Updating cycle {} status to: {}", id, status);

        CycleStatus cycleStatus = CycleStatus.valueOf(status);
        TankCycle cycle = tankCycleService.updateCycleStatus(id, cycleStatus);
        return ResponseEntity.ok(toResponse(cycle));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<TankCycleResponse> recordReturnDate(
            @PathVariable Long id,
            @RequestParam String returnedAt) {
        log.info("Recording return date for cycle: {}", id);

        LocalDateTime returnDate = LocalDateTime.parse(returnedAt);
        TankCycle cycle = tankCycleService.recordReturnDate(id, returnDate);
        return ResponseEntity.ok(toResponse(cycle));
    }

    @GetMapping("/{id}/step-durations")
    public ResponseEntity<List<StepDurationDetail>> getStepDurations(@PathVariable Long id) {
        log.info("Fetching step durations for cycle: {}", id);

        List<StepDurationDetail> stepDurations = tankCycleService.calculateStepDurations(id);
        return ResponseEntity.ok(stepDurations);
    }

    private TankCycleResponse toResponse(TankCycle cycle) {
        return new TankCycleResponse(
                cycle.getId(),
                cycle.getPublicCode(),
                cycle.getAssetId(),
                cycle.getStartedAt(),
                cycle.getReturnedToSupplierAt(),
                cycle.getDeadlineAt(),
                cycle.getStatus().name(),
                cycle.getCurrentStepNumber(),
                cycle.getDurationDays(),
                cycle.getDaysRemaining(),
                cycle.getPenaltyAmount(),
                cycle.getIsPenaltyApplied(),
                cycle.getVersion(),
                cycle.getCreatedAt(),
                cycle.getUpdatedAt()
        );
    }
}
