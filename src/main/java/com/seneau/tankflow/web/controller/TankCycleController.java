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
        updateCycleStatusBasedOnDeadline(cycle);
        TankCycleResponse response = new TankCycleResponse();
        response.setId(cycle.getId());
        response.setPublicCode(cycle.getPublicCode());
        response.setAssetId(cycle.getAssetId());
        response.setStartedAt(cycle.getStartedAt());
        response.setReturnedToSupplierAt(cycle.getReturnedToSupplierAt());
        response.setDeadlineAt(cycle.getDeadlineAt());
        response.setStatus(cycle.getStatus().name());
        response.setCurrentStepNumber(cycle.getCurrentStepNumber());
        int durationDays = calculateDurationDays(cycle);
        int daysRemaining = calculateDaysRemaining(cycle);
        response.setDurationDays(durationDays);
        response.setDaysRemaining(daysRemaining);
        response.setAlertLevel(calculateAlertLevel(daysRemaining));
        response.setLocalisation(cycle.getLocalisation());
        response.setZone(cycle.getZone());
        response.setPenaltyAmount(cycle.getPenaltyAmount());
        response.setIsPenaltyApplied(cycle.getIsPenaltyApplied());
        response.setVersion(cycle.getVersion());
        response.setCreatedAt(cycle.getCreatedAt());
        response.setUpdatedAt(cycle.getUpdatedAt());
        return response;
    }

    private Integer calculateDurationDays(TankCycle cycle) {
        if (cycle.getStartedAt() == null) return 0;
        java.time.LocalDateTime endDate = cycle.getReturnedToSupplierAt() != null
            ? cycle.getReturnedToSupplierAt()
            : java.time.LocalDateTime.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(cycle.getStartedAt(), endDate);
    }

    private Integer calculateDaysRemaining(TankCycle cycle) {
        if (cycle.getDeadlineAt() == null) return 0;
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDateTime.now(), cycle.getDeadlineAt());
        return (int) daysRemaining;
    }

    private void updateCycleStatusBasedOnDeadline(TankCycle cycle) {
        if (cycle.getStatus() == com.seneau.tankflow.data.enumeration.CycleStatus.COMPLETED) {
            return;
        }
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDateTime.now(), cycle.getDeadlineAt());
        if (daysRemaining < 0) {
            cycle.setStatus(com.seneau.tankflow.data.enumeration.CycleStatus.OVERDUE);
        }
    }

    private String calculateAlertLevel(int daysRemaining) {
        if (daysRemaining < 0) {
            return "OVERDUE";
        } else if (daysRemaining <= 5) {
            return "J_MINUS_5";
        } else if (daysRemaining <= 10) {
            return "J_MINUS_10";
        } else if (daysRemaining <= 15) {
            return "J_MINUS_15";
        } else {
            return "NONE";
        }
    }
}
