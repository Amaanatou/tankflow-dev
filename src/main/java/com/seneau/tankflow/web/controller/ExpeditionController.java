package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.model.Expedition;
import com.seneau.tankflow.service.interfaces.ExpeditionService;
import com.seneau.tankflow.web.dto.request.CreateExpeditionRequest;
import com.seneau.tankflow.web.dto.request.MarkExpeditionAsArrivedRequest;
import com.seneau.tankflow.web.dto.response.ExpeditionResponse;
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
@RequestMapping("/api/v1/expeditions")
@RequiredArgsConstructor
public class ExpeditionController {

    private final ExpeditionService expeditionService;

    @PostMapping
    public ResponseEntity<ExpeditionResponse> createExpedition(
            @Valid @RequestBody CreateExpeditionRequest request) {
        log.info("Creating expedition: {}", request.getReference());

        Expedition expedition = expeditionService.createExpedition(
                request.getReference(),
                request.getType(),
                request.getOrigine(),
                request.getDestination(),
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
        return new ExpeditionResponse(
                expedition.getId(),
                expedition.getReference(),
                expedition.getType(),
                expedition.getOrigine(),
                expedition.getDestination(),
                expedition.getStatut(),
                expedition.getDateDepart(),
                expedition.getDateArrivee(),
                expedition.getCreatedAt(),
                expedition.getUpdatedAt()
        );
    }
}
