package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.model.ExpeditionTank;

import java.util.List;
import java.util.Optional;

public interface ExpeditionTankService {

    ExpeditionTank addCycleToExpedition(Long expeditionId, Long cycleId, Long tankId);

    Optional<ExpeditionTank> getExpeditionTankById(Long id);

    List<ExpeditionTank> getCyclesByExpedition(Long expeditionId);

    List<ExpeditionTank> getExpeditionsByCycle(Long cycleId);

    List<ExpeditionTank> getExpeditionsByTank(Long tankId);

    ExpeditionTank markAsSelected(Long id);

    void removeCycleFromExpedition(Long id);

    long countCyclesInExpedition(Long expeditionId);

    long getTotalExpeditionTankCount();
}
