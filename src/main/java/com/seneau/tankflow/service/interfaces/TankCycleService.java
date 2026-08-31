package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;

import java.util.List;
import java.util.Optional;

public interface TankCycleService {

    TankCycle createCycle(Long assetId, Long supplierId);

    Optional<TankCycle> getCycleByPublicCode(String publicCode);

    Optional<TankCycle> getCycleById(Long id);

    List<TankCycle> getCyclesByAsset(Long assetId);

    List<TankCycle> getCyclesByStatus(CycleStatus status);

    TankCycle transitionToStep(Long cycleId, Integer stepNumber);

    TankCycle completeCycle(Long cycleId);

    List<TankCycle> getOverdueCycles();

    List<TankCycle> getCyclesNearDeadline(Integer daysThreshold);

    TankCycle calculatePenalty(Long cycleId);

    long getActiveStepCount();

    long getCompletedCycleCount();
}
