package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TankCycleServiceImpl implements TankCycleService {

    private final TankCycleRepository cycleRepository;
    private static final int CYCLE_DEADLINE_DAYS = 180;
    private static final BigDecimal DAILY_PENALTY = new BigDecimal("3.00");

    @Override
    public TankCycle createCycle(Long assetId, Long supplierId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(CYCLE_DEADLINE_DAYS);
        String publicCode = generatePublicCode(assetId);

        TankCycle cycle = new TankCycle();
        cycle.setAssetId(assetId);
        cycle.setPublicCode(publicCode);
        cycle.setStartedAt(now);
        cycle.setDeadlineAt(deadline);
        cycle.setStatus(CycleStatus.IN_PROGRESS);
        cycle.setCurrentStepNumber(1);
        cycle.setDurationDays(0);
        cycle.setDaysRemaining(CYCLE_DEADLINE_DAYS);
        cycle.setPenaltyAmount(BigDecimal.ZERO);
        cycle.setIsPenaltyApplied(false);

        return cycleRepository.save(cycle);
    }

    @Override
    public Optional<TankCycle> getCycleByPublicCode(String publicCode) {
        return cycleRepository.findByPublicCode(publicCode);
    }

    @Override
    public Optional<TankCycle> getCycleById(Long id) {
        return cycleRepository.findById(id);
    }

    @Override
    public List<TankCycle> getCyclesByAsset(Long assetId) {
        return cycleRepository.findByAssetId(assetId);
    }

    @Override
    public List<TankCycle> getCyclesByStatus(CycleStatus status) {
        return cycleRepository.findByStatus(status);
    }

    @Override
    public TankCycle transitionToStep(Long cycleId, Integer stepNumber) {
        TankCycle cycle = cycleRepository.findById(cycleId)
            .orElseThrow(() -> new IllegalArgumentException("Cycle not found"));

        if (stepNumber <= 0 || stepNumber > 9) {
            throw new IllegalArgumentException("Invalid step number");
        }

        if (!canTransitionToStep(cycle.getCurrentStepNumber(), stepNumber)) {
            throw new IllegalStateException("Cannot transition to step " + stepNumber);
        }

        cycle.setCurrentStepNumber(stepNumber);
        if (stepNumber == 9) {
            return completeCycle(cycleId);
        }

        return cycleRepository.save(cycle);
    }

    @Override
    public TankCycle completeCycle(Long cycleId) {
        TankCycle cycle = cycleRepository.findById(cycleId)
            .orElseThrow(() -> new IllegalArgumentException("Cycle not found"));

        LocalDateTime now = LocalDateTime.now();
        cycle.setReturnedToSupplierAt(now);
        cycle.setStatus(CycleStatus.COMPLETED);
        cycle.setCurrentStepNumber(9);

        long durationDays = ChronoUnit.DAYS.between(cycle.getStartedAt(), now);
        cycle.setDurationDays((int) durationDays);

        return cycleRepository.save(calculatePenalty(cycleId));
    }

    @Override
    public List<TankCycle> getOverdueCycles() {
        return cycleRepository.findByStatus(CycleStatus.IN_PROGRESS).stream()
            .filter(c -> c.getDeadlineAt().isBefore(LocalDateTime.now()))
            .toList();
    }

    @Override
    public List<TankCycle> getCyclesNearDeadline(Integer daysThreshold) {
        return cycleRepository.findCyclesNearDeadline(daysThreshold, CycleStatus.IN_PROGRESS);
    }

    @Override
    public TankCycle calculatePenalty(Long cycleId) {
        TankCycle cycle = cycleRepository.findById(cycleId)
            .orElseThrow(() -> new IllegalArgumentException("Cycle not found"));

        if (cycle.getReturnedToSupplierAt() == null) {
            return cycle;
        }

        long daysOverdue = ChronoUnit.DAYS.between(cycle.getDeadlineAt(), cycle.getReturnedToSupplierAt());

        if (daysOverdue > 0) {
            BigDecimal penalty = DAILY_PENALTY.multiply(new BigDecimal(daysOverdue));
            cycle.setPenaltyAmount(penalty);
            cycle.setIsPenaltyApplied(true);
        }

        return cycleRepository.save(cycle);
    }

    @Override
    public long getActiveStepCount() {
        return (long) cycleRepository.findByStatus(CycleStatus.IN_PROGRESS).size();
    }

    @Override
    public long getCompletedCycleCount() {
        return (long) cycleRepository.findByStatus(CycleStatus.COMPLETED).size();
    }

    private String generatePublicCode(Long assetId) {
        int year = LocalDateTime.now().getYear();
        String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("TANK-%d-%d-%s", assetId, year, randomCode);
    }

    private boolean canTransitionToStep(Integer currentStep, Integer nextStep) {
        return nextStep > currentStep && nextStep <= 9;
    }
}
