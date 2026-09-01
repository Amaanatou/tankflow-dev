package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TankCycleService {
    TankCycle createCycle(Long assetId, LocalDateTime startedAt, LocalDateTime deadlineAt);
    Optional<TankCycle> getCycleById(Long id);
    List<TankCycle> getCyclesByAssetId(Long assetId);
    List<TankCycle> getCyclesByStatus(CycleStatus status);
    TankCycle updateCycleStatus(Long id, CycleStatus status);
    TankCycle recordReturnDate(Long id, LocalDateTime returnedAt);
    Optional<TankCycle> getCycleByPublicCode(String publicCode);
}
