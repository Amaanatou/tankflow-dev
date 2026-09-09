package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.model.WorkflowEvent;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.data.repository.WorkflowEventRepository;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import com.seneau.tankflow.web.dto.response.StepDurationDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TankCycleServiceImpl implements TankCycleService {

    private final TankCycleRepository tankCycleRepository;
    private final WorkflowEventRepository workflowEventRepository;

    @Override
    public TankCycle createCycle(Long assetId, LocalDateTime startedAt, LocalDateTime deadlineAt) {
        log.info("Creating cycle for asset ID={}, started={}, deadline={}", assetId, startedAt, deadlineAt);

        TankCycle cycle = new TankCycle();
        cycle.setAssetId(assetId);
        cycle.setStartedAt(startedAt);
        cycle.setDeadlineAt(deadlineAt);
        cycle.setStatus(CycleStatus.IN_PROGRESS);
        cycle.setCurrentStepNumber(1);
        cycle.setIsPenaltyApplied(false);

        return tankCycleRepository.save(cycle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankCycle> getCycleById(Long id) {
        log.info("Fetching cycle ID={}", id);
        return tankCycleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankCycle> getCyclesByAssetId(Long assetId) {
        log.info("Fetching cycles for asset ID={}", assetId);
        return tankCycleRepository.findByAssetId(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankCycle> getCyclesByStatus(CycleStatus status) {
        log.info("Fetching cycles with status={}", status);
        return tankCycleRepository.findByStatus(status);
    }

    @Override
    public TankCycle updateCycleStatus(Long id, CycleStatus status) {
        log.info("Updating cycle ID={} with status={}", id, status);

        TankCycle cycle = tankCycleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cycle not found with ID: " + id));

        cycle.setStatus(status);
        return tankCycleRepository.save(cycle);
    }

    @Override
    public TankCycle recordReturnDate(Long id, LocalDateTime returnedAt) {
        log.info("Recording return date for cycle ID={}, returnedAt={}", id, returnedAt);

        TankCycle cycle = tankCycleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cycle not found with ID: " + id));

        cycle.setReturnedToSupplierAt(returnedAt);
        cycle.setStatus(CycleStatus.COMPLETED);

        return tankCycleRepository.save(cycle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankCycle> getCycleByPublicCode(String publicCode) {
        log.info("Fetching cycle by public code={}", publicCode);
        return tankCycleRepository.findByPublicCode(publicCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StepDurationDetail> calculateStepDurations(Long cycleId) {
        log.info("Calculating step durations for cycle ID={}", cycleId);

        TankCycle cycle = tankCycleRepository.findById(cycleId)
                .orElseThrow(() -> new IllegalArgumentException("Cycle not found with ID: " + cycleId));

        List<WorkflowEvent> events = workflowEventRepository.findByCycleIdOrderByCreatedAtAsc(cycleId);

        if (events.isEmpty()) {
            log.warn("No workflow events found for cycle {}", cycleId);
            return new ArrayList<>();
        }

        Map<Integer, List<WorkflowEvent>> eventsByStep = events.stream()
                .collect(Collectors.groupingBy(WorkflowEvent::getStepNumber));

        List<StepDurationDetail> result = new ArrayList<>();

        for (int stepNum = 1; stepNum <= 9; stepNum++) {
            List<WorkflowEvent> stepEvents = eventsByStep.getOrDefault(stepNum, new ArrayList<>());

            if (stepEvents.isEmpty()) {
                continue;
            }

            WorkflowEvent firstEvent = stepEvents.get(0);
            LocalDateTime startedAt = firstEvent.getEventTimestamp();
            LocalDateTime completedAt = null;
            String status = "IN_PROGRESS";

            if (stepEvents.size() > 1) {
                WorkflowEvent lastEvent = stepEvents.get(stepEvents.size() - 1);
                completedAt = lastEvent.getEventTimestamp();
                status = "COMPLETED";
            } else if (stepNum < cycle.getCurrentStepNumber() ||
                       (stepNum == cycle.getCurrentStepNumber() && cycle.getStatus() == CycleStatus.COMPLETED)) {
                completedAt = cycle.getReturnedToSupplierAt();
                status = "COMPLETED";
            }

            LocalDateTime calculatedEnd = completedAt != null ? completedAt : LocalDateTime.now();
            long durationMinutes = ChronoUnit.MINUTES.between(startedAt, calculatedEnd);
            double durationDaysDecimal = durationMinutes / (24.0 * 60.0);
            int durationDaysRounded = (int) Math.round(durationDaysDecimal);

            StepDurationDetail detail = StepDurationDetail.builder()
                    .stepNumber(stepNum)
                    .stepLabel(getStepLabel(stepNum))
                    .startedAt(startedAt)
                    .completedAt(completedAt)
                    .durationDays(durationDaysDecimal)
                    .durationDaysRounded(durationDaysRounded)
                    .status(status)
                    .build();

            result.add(detail);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankCycle> getActiveCycleByAssetId(Long assetId) {
        log.info("Fetching active cycle for asset ID={}", assetId);
        List<TankCycle> activeCycles = tankCycleRepository.findActiveCyclesByAssetId(assetId);
        return activeCycles.isEmpty() ? Optional.empty() : Optional.of(activeCycles.get(0));
    }

    private String getStepLabel(int stepNumber) {
        return switch (stepNumber) {
            case 1 -> "Expédition fournisseur";
            case 2 -> "Réception site principal";
            case 3 -> "Acheminement vers usine";
            case 4 -> "Réception usine";
            case 5 -> "Mise en utilisation";
            case 6 -> "Fin utilisation";
            case 7 -> "Retour vers site principal";
            case 8 -> "Réception tanks vides";
            case 9 -> "Expédition retour fournisseur";
            default -> "Étape " + stepNumber;
        };
    }
}
