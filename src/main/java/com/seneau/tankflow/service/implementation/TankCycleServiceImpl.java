package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TankCycleServiceImpl implements TankCycleService {

    private final TankCycleRepository tankCycleRepository;

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
}
