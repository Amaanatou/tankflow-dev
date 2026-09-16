package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.model.Expedition;
import com.seneau.tankflow.data.model.TankAsset;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.TankAssetRepository;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.service.interfaces.ExpeditionService;
import com.seneau.tankflow.web.dto.request.CreateExpeditionRequest;
import com.seneau.tankflow.web.dto.request.MarkExpeditionAsArrivedRequest;
import com.seneau.tankflow.web.dto.response.ExpeditionResponse;
import com.seneau.tankflow.web.dto.response.ExpeditionTankResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/expeditions")
@RequiredArgsConstructor
public class ExpeditionController {

    private final ExpeditionService expeditionService;
    private final TankAssetRepository tankAssetRepository;
    private final TankCycleRepository tankCycleRepository;

    @PostMapping
    public ResponseEntity<ExpeditionResponse> createExpedition(
            @Valid @RequestBody CreateExpeditionRequest request) {
        log.info("Creating expedition: {}", request.getReference());

        Expedition expedition = expeditionService.createExpeditionWithEnums(
                request.getReference(),
                request.getType(),
                request.getOrigine(),
                request.getDestination(),
                request.getTransporteur(),
                request.getStatut(),
                request.getDateDepart()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(expedition));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpeditionResponse> getExpeditionById(@PathVariable Long id) {
        log.info("Fetching expedition: {}", id);

        Expedition expedition = expeditionService.getExpeditionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expedition not found with ID: " + id));

        return ResponseEntity.ok(toResponse(expedition));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<ExpeditionResponse> getExpeditionByReference(@PathVariable String reference) {
        log.info("Fetching expedition by reference: {}", reference);

        Expedition expedition = expeditionService.getExpeditionByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Expedition not found with reference: " + reference));

        return ResponseEntity.ok(toResponse(expedition));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ExpeditionResponse>> getExpeditionsByStatut(@PathVariable String statut) {
        log.info("Fetching expeditions by statut: {}", statut);

        List<Expedition> expeditions = expeditionService.getExpeditionsByStatut(statut);
        List<ExpeditionResponse> responses = expeditions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/origine/{origine}")
    public ResponseEntity<List<ExpeditionResponse>> getExpeditionsByOrigine(@PathVariable String origine) {
        log.info("Fetching expeditions from: {}", origine);

        List<Expedition> expeditions = expeditionService.getExpeditionsByOrigine(origine);
        List<ExpeditionResponse> responses = expeditions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<ExpeditionResponse>> getExpeditionsByDestination(@PathVariable String destination) {
        log.info("Fetching expeditions to: {}", destination);

        List<Expedition> expeditions = expeditionService.getExpeditionsByDestination(destination);
        List<ExpeditionResponse> responses = expeditions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<ExpeditionResponse> updateExpeditionStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        log.info("Updating expedition {} statut to: {}", id, statut);

        Expedition expedition = expeditionService.updateExpeditionStatut(id, statut);
        return ResponseEntity.ok(toResponse(expedition));
    }

    @PutMapping("/{id}/arrival")
    public ResponseEntity<ExpeditionResponse> markAsArrived(
            @PathVariable Long id,
            @Valid @RequestBody MarkExpeditionAsArrivedRequest request) {
        log.info("Marking expedition {} as arrived at {}", id, request.getDateArrivee());

        Expedition expedition = expeditionService.updateExpeditionArrival(id, request.getDateArrivee());
        return ResponseEntity.ok(toResponse(expedition));
    }

    private ExpeditionResponse toResponse(Expedition expedition) {
        ExpeditionResponse response = new ExpeditionResponse();
        response.setId(expedition.getId());
        response.setReference(expedition.getReference());
        response.setType(expedition.getType());
        response.setOrigine(expedition.getOrigine());
        response.setDestination(expedition.getDestination());
        response.setTransporteur(expedition.getTransporteur());
        response.setStatut(expedition.getStatut());
        response.setDateDepart(expedition.getDateDepart());
        response.setDateArrivee(expedition.getDateArrivee());
        response.setTanks(expedition.getTanks() != null
            ? expedition.getTanks().stream()
                .map(this::buildExpeditionTankResponse)
                .toList()
            : null
        );
        response.setCreatedAt(expedition.getCreatedAt());
        response.setUpdatedAt(expedition.getUpdatedAt());
        return response;
    }

    private ExpeditionTankResponse buildExpeditionTankResponse(com.seneau.tankflow.data.model.ExpeditionTank et) {
        ExpeditionTankResponse response = new ExpeditionTankResponse();
        response.setId(et.getId());
        response.setExpeditionId(et.getExpedition() != null ? et.getExpedition().getId() : null);
        response.setCycleId(et.getCycleId());
        response.setTankId(et.getTankId());
        response.setSelected(et.getSelected());
        response.setCreatedAt(et.getCreatedAt());

        Optional<TankAsset> tank = tankAssetRepository.findById(et.getTankId());
        tank.ifPresent(t -> response.setManufacturerSerial(t.getManufacturerSerial()));

        if (et.getCycleId() != null) {
            Optional<TankCycle> cycle = tankCycleRepository.findById(et.getCycleId());
            cycle.ifPresent(c -> response.setPublicCode(c.getPublicCode()));
        }

        return response;
    }
}
