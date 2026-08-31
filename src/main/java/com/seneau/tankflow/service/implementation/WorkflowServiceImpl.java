package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.enumeration.WorkflowStep;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.model.WorkflowEvent;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.data.repository.WorkflowEventRepository;
import com.seneau.tankflow.service.interfaces.WorkflowService;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowEventRepository eventRepository;
    private final TankCycleRepository cycleRepository;

    @Override
    public WorkflowEvent recordEvent(Long cycleId, WorkflowStep step, Long locationId, Long zoneId,
                                     Long performedByUserId, String idempotencyKey) {
        log.info("Recording workflow event for cycle: {}, step: {}", cycleId, step);

        TankCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));

        if (eventRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.debug("Event already recorded (idempotency): {}", idempotencyKey);
            return eventRepository.findByIdempotencyKey(idempotencyKey).get();
        }

        if (!canTransitionToStep(cycleId, step.getStepNumber())) {
            throw new IllegalArgumentException("Cannot transition to step " + step.getStepNumber());
        }

        WorkflowEvent event = new WorkflowEvent();
        event.setCycleId(cycleId);
        event.setStepNumber(step.getStepNumber());
        event.setEventType(step);
        event.setLocationId(locationId);
        event.setZoneId(zoneId);
        event.setPerformedByUserId(performedByUserId);
        event.setEventTimestamp(LocalDateTime.now());
        event.setIdempotencyKey(idempotencyKey);

        WorkflowEvent saved = eventRepository.save(event);

        cycle.setCurrentStepNumber(step.getStepNumber());
        if (step.getStepNumber() == WorkflowStep.RETURN_TO_SUPPLIER.getStepNumber()) {
            cycle.setStatus(CycleStatus.COMPLETED);
            cycle.setReturnedToSupplierAt(LocalDateTime.now());
        }
        cycleRepository.save(cycle);

        log.info("Workflow event recorded: {} for cycle {}", step, cycleId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowEvent> getEventsByCycle(Long cycleId) {
        log.debug("Fetching events for cycle: {}", cycleId);
        return eventRepository.findByCycleIdOrderByCreatedAtAsc(cycleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canTransitionToStep(Long cycleId, Integer nextStepNumber) {
        log.debug("Checking if cycle {} can transition to step {}", cycleId, nextStepNumber);

        TankCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));

        int currentStep = cycle.getCurrentStepNumber();

        if (cycle.getStatus() == CycleStatus.COMPLETED) {
            log.warn("Cannot transition: cycle {} is already completed", cycleId);
            return false;
        }

        return nextStepNumber > currentStep && nextStepNumber <= 9;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowStep getCurrentStep(Long cycleId) {
        log.debug("Fetching current step for cycle: {}", cycleId);

        TankCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));

        return WorkflowStep.fromStepNumber(cycle.getCurrentStepNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowStep getNextStep(Long cycleId) {
        log.debug("Fetching next step for cycle: {}", cycleId);

        TankCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));

        int nextStepNumber = cycle.getCurrentStepNumber() + 1;
        if (nextStepNumber > 9) {
            return null;
        }

        return WorkflowStep.fromStepNumber(nextStepNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIdempotencyKeyProcessed(String idempotencyKey) {
        log.debug("Checking if idempotency key is processed: {}", idempotencyKey);
        return eventRepository.existsByIdempotencyKey(idempotencyKey);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalEventCount() {
        long count = eventRepository.count();
        log.debug("Total event count: {}", count);
        return count;
    }
}
