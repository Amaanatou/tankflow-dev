package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.model.TankAsset;
import com.seneau.tankflow.service.interfaces.TankAssetService;
import com.seneau.tankflow.web.dto.request.CreateTankRequest;
import com.seneau.tankflow.web.dto.response.TankAssetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/tanks")
@RequiredArgsConstructor
public class TankAssetController {

    private final TankAssetService tankAssetService;

    @PostMapping
    public ResponseEntity<TankAssetResponse> createTank(
            @Valid @RequestBody CreateTankRequest request) {
        log.info("Creating tank: manufacturerSerial={}", request.getManufacturerSerial());

        TankAsset tank = tankAssetService.createTank(
                request.getManufacturerSerial(),
                request.getSupplierId(),
                request.getHasSafetyBell(),
                request.getTankStatus(),
                request.getNotes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(tank));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankAssetResponse> getTankById(@PathVariable Long id) {
        log.info("Fetching tank: {}", id);

        TankAsset tank = tankAssetService.getTankById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tank not found with ID: " + id));

        return ResponseEntity.ok(toResponse(tank));
    }

    @GetMapping("/serial/{manufacturerSerial}")
    public ResponseEntity<TankAssetResponse> getTankByManufacturerSerial(@PathVariable String manufacturerSerial) {
        log.info("Fetching tank by manufacturer serial: {}", manufacturerSerial);

        TankAsset tank = tankAssetService.getTankByManufacturerSerial(manufacturerSerial)
                .orElseThrow(() -> new IllegalArgumentException("Tank not found with serial: " + manufacturerSerial));

        return ResponseEntity.ok(toResponse(tank));
    }

    @GetMapping
    public ResponseEntity<List<TankAssetResponse>> getAllTanks() {
        log.info("Fetching all tanks");

        List<TankAsset> tanks = tankAssetService.getAllTanks();
        List<TankAssetResponse> responses = tanks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TankAssetResponse> updateTank(
            @PathVariable Long id,
            @Valid @RequestBody CreateTankRequest request) {
        log.info("Updating tank: id={}, manufacturerSerial={}", id, request.getManufacturerSerial());

        TankAsset tank = tankAssetService.updateTank(
                id,
                request.getSupplierId(),
                request.getHasSafetyBell(),
                request.getTankStatus(),
                request.getNotes()
        );

        return ResponseEntity.ok(toResponse(tank));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTank(@PathVariable Long id) {
        log.info("Deleting tank: {}", id);

        tankAssetService.deleteTank(id);
        return ResponseEntity.noContent().build();
    }

    private TankAssetResponse toResponse(TankAsset tank) {
        return new TankAssetResponse(
                tank.getId(),
                tank.getManufacturerSerial(),
                tank.getSupplierId(),
                tank.getHasSafetyBell(),
                tank.getTankStatus(),
                tank.getNotes(),
                tank.getCreatedAt(),
                tank.getUpdatedAt()
        );
    }
}
