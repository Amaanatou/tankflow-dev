package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.model.ExpeditionTank;
import com.seneau.tankflow.service.interfaces.ExpeditionTankService;
import com.seneau.tankflow.web.dto.request.AddCycleToExpeditionRequest;
import com.seneau.tankflow.web.dto.request.AddTanksBySerialRequest;
import com.seneau.tankflow.web.dto.response.AddTanksToExpeditionResult;
import com.seneau.tankflow.web.dto.response.ExpeditionTankResponse;
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
@RequestMapping("/api/v1/expeditions/{expeditionId}/cycles")
@RequiredArgsConstructor
public class ExpeditionTankController {

    private final ExpeditionTankService expeditionTankService;

    @PostMapping
    public ResponseEntity<ExpeditionTankResponse> addCycleToExpedition(
            @PathVariable Long expeditionId,
            @Valid @RequestBody AddCycleToExpeditionRequest request) {
        log.info("Adding cycle {} to expedition {}", request.getCycleId(), expeditionId);

        ExpeditionTank expeditionTank = expeditionTankService.addCycleToExpedition(
                expeditionId,
                request.getCycleId(),
                request.getTankId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(expeditionTank));
    }

    @PostMapping("/by-serial")
    public ResponseEntity<AddTanksToExpeditionResult> addTanksBySerialNumbers(
            @PathVariable Long expeditionId,
            @Valid @RequestBody AddTanksBySerialRequest request) {
        log.info("Adding tanks by serial numbers to expedition {}: {}", expeditionId, request.getManufacturerSerials());

        AddTanksToExpeditionResult result = expeditionTankService.addTanksBySerialNumbers(
                expeditionId,
                request.getManufacturerSerials()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<ExpeditionTankResponse>> getCyclesByExpedition(@PathVariable Long expeditionId) {
        log.info("Fetching cycles for expedition {}", expeditionId);

        List<ExpeditionTank> expeditionTanks = expeditionTankService.getCyclesByExpedition(expeditionId);
        List<ExpeditionTankResponse> responses = expeditionTanks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpeditionTankResponse> getExpeditionTankById(
            @PathVariable Long expeditionId,
            @PathVariable Long id) {
        log.info("Fetching expedition-tank {} for expedition {}", id, expeditionId);

        ExpeditionTank expeditionTank = expeditionTankService.getExpeditionTankById(id)
                .orElseThrow(() -> new IllegalArgumentException("ExpeditionTank not found with ID: " + id));

        return ResponseEntity.ok(toResponse(expeditionTank));
    }

    @PutMapping("/{id}/select")
    public ResponseEntity<ExpeditionTankResponse> markAsSelected(
            @PathVariable Long expeditionId,
            @PathVariable Long id) {
        log.info("Marking expedition-tank {} as selected", id);

        ExpeditionTank expeditionTank = expeditionTankService.markAsSelected(id);
        return ResponseEntity.ok(toResponse(expeditionTank));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCycleFromExpedition(
            @PathVariable Long expeditionId,
            @PathVariable Long id) {
        log.info("Removing cycle from expedition {} (ID: {})", expeditionId, id);

        expeditionTankService.removeCycleFromExpedition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCyclesInExpedition(@PathVariable Long expeditionId) {
        log.info("Counting cycles in expedition {}", expeditionId);

        long count = expeditionTankService.countCyclesInExpedition(expeditionId);
        return ResponseEntity.ok(count);
    }

    private ExpeditionTankResponse toResponse(ExpeditionTank expeditionTank) {
        return new ExpeditionTankResponse(
                expeditionTank.getId(),
                expeditionTank.getExpeditionId(),
                expeditionTank.getCycleId(),
                expeditionTank.getTankId(),
                expeditionTank.getSelected(),
                expeditionTank.getCreatedAt()
        );
    }
}
