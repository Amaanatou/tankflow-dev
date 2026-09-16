package com.seneau.tankflow.web.controller;

import com.seneau.tankflow.data.enumeration.WorkflowStep;
import com.seneau.tankflow.data.model.User;
import com.seneau.tankflow.data.model.WorkflowEvent;
import com.seneau.tankflow.data.repository.UserRepository;
import com.seneau.tankflow.service.interfaces.WorkflowService;
import com.seneau.tankflow.web.dto.request.RecordWorkflowEventRequest;
import com.seneau.tankflow.web.dto.response.WorkflowEventResponse;
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
@RequestMapping("/api/v1/cycles/{cycleId}/events")
@RequiredArgsConstructor
public class WorkflowEventController {

    private final WorkflowService workflowService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<WorkflowEventResponse> recordEvent(
            @PathVariable Long cycleId,
            @Valid @RequestBody RecordWorkflowEventRequest request) {
        log.info("Recording workflow event for cycle: {}", cycleId);

        WorkflowStep step = WorkflowStep.valueOf(request.getStep().toUpperCase());

        WorkflowEvent event = workflowService.recordEvent(
                cycleId,
                step,
                request.getSite(),
                request.getZone(),
                request.getPerformedByUserId(),
                request.getIdempotencyKey(),
                request.getTankCondition(),
                request.getSafetyBellPresent(),
                request.getDocumentReference(),
                request.getComment()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(event));
    }

    @GetMapping
    public ResponseEntity<List<WorkflowEventResponse>> getEventsByCycle(@PathVariable Long cycleId) {
        log.info("Fetching events for cycle: {}", cycleId);

        List<WorkflowEvent> events = workflowService.getEventsByCycle(cycleId);
        List<WorkflowEventResponse> responses = events.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<WorkflowEventResponse> getEventById(
            @PathVariable Long cycleId,
            @PathVariable Long eventId) {
        log.info("Fetching event {} for cycle: {}", eventId, cycleId);

        WorkflowEvent event = new WorkflowEvent();
        event.setId(eventId);

        return ResponseEntity.ok(toResponse(event));
    }

    @GetMapping("/current-step")
    public ResponseEntity<String> getCurrentStep(@PathVariable Long cycleId) {
        log.info("Getting current step for cycle: {}", cycleId);

        WorkflowStep currentStep = workflowService.getCurrentStep(cycleId);
        return ResponseEntity.ok(currentStep.getLabel());
    }

    @GetMapping("/next-step")
    public ResponseEntity<String> getNextStep(@PathVariable Long cycleId) {
        log.info("Getting next step for cycle: {}", cycleId);

        WorkflowStep nextStep = workflowService.getNextStep(cycleId);
        if (nextStep == null) {
            return ResponseEntity.ok("Cycle terminé");
        }
        return ResponseEntity.ok(nextStep.getLabel());
    }

    @GetMapping("/can-transition/{stepNumber}")
    public ResponseEntity<Boolean> canTransitionToStep(
            @PathVariable Long cycleId,
            @PathVariable Integer stepNumber) {
        log.info("Checking if cycle {} can transition to step {}", cycleId, stepNumber);

        boolean canTransition = workflowService.canTransitionToStep(cycleId, stepNumber);
        return ResponseEntity.ok(canTransition);
    }

    @GetMapping("/idempotency/{key}/processed")
    public ResponseEntity<Boolean> isIdempotencyKeyProcessed(
            @PathVariable Long cycleId,
            @PathVariable String key) {
        log.info("Checking if idempotency key is processed: {}", key);

        boolean isProcessed = workflowService.isIdempotencyKeyProcessed(key);
        return ResponseEntity.ok(isProcessed);
    }

    private WorkflowEventResponse toResponse(WorkflowEvent event) {
        WorkflowEventResponse response = new WorkflowEventResponse();
        response.setId(event.getId());
        response.setCycleId(event.getCycleId());
        response.setStepNumber(event.getStepNumber());
        response.setEventType(event.getEventType() != null ? mapWorkflowStepToEventType(event.getEventType()) : null);
        response.setSite(event.getSite());
        response.setZone(event.getZone());
        response.setPerformedByUsername(getUsernameFromId(event.getPerformedByUserId()));
        response.setEventTimestamp(event.getEventTimestamp());
        response.setTankCondition(event.getTankCondition() != null ? event.getTankCondition().name() : null);
        response.setSafetyBellPresent(event.getSafetyBellPresent());
        response.setDocumentReference(event.getDocumentReference());
        response.setComment(event.getComment());
        response.setIdempotencyKey(event.getIdempotencyKey());
        response.setMetadata(event.getMetadata());
        response.setCreatedAt(event.getCreatedAt());
        return response;
    }

    private String getUsernameFromId(Long userId) {
        if (userId == null) return null;
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getUsername).orElse("Unknown");
    }

    private String mapWorkflowStepToEventType(WorkflowStep step) {
        if (step == null) return null;
        return switch (step) {
            case SHIPMENT_SENT -> "SHIPMENT";
            case MAIN_RECEPTION -> "RECEPTION";
            case ROUTED_TO_FACTORY -> "TRANSFER";
            case FACTORY_RECEPTION -> "RECEPTION";
            case START_USAGE -> "USE_START";
            case END_USAGE -> "USE_END";
            case RETURN_TO_MAIN -> "TRANSFER";
            case MAIN_RETURN_RECEPTION -> "RECEPTION";
            case RETURN_TO_SUPPLIER -> "SHIPMENT";
        };
    }
}
