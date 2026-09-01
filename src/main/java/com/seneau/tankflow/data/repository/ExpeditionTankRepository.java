package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.ExpeditionTank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpeditionTankRepository extends JpaRepository<ExpeditionTank, Long> {

    List<ExpeditionTank> findByExpeditionId(Long expeditionId);

    List<ExpeditionTank> findByCycleId(Long cycleId);

    List<ExpeditionTank> findByTankId(Long tankId);

    Optional<ExpeditionTank> findByExpeditionIdAndCycleId(Long expeditionId, Long cycleId);

    boolean existsByExpeditionIdAndCycleId(Long expeditionId, Long cycleId);

    long countByExpeditionId(Long expeditionId);
}
