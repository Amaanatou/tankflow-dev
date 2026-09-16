package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.ExpeditionTank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpeditionTankRepository extends JpaRepository<ExpeditionTank, Long> {

    // Navigue via la relation @ManyToOne: expedition.id au lieu de expeditionId
    List<ExpeditionTank> findByExpedition_Id(Long expeditionId);

    List<ExpeditionTank> findByCycleId(Long cycleId);

    List<ExpeditionTank> findByTankId(Long tankId);

    // Navigue via la relation pour la condition compound
    Optional<ExpeditionTank> findByExpedition_IdAndCycleId(Long expeditionId, Long cycleId);

    boolean existsByExpedition_IdAndCycleId(Long expeditionId, Long cycleId);

    long countByExpedition_Id(Long expeditionId);
}
