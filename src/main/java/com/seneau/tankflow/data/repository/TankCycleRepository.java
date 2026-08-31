package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TankCycleRepository extends JpaRepository<TankCycle, Long> {

    Optional<TankCycle> findByPublicCode(String publicCode);

    List<TankCycle> findByAssetId(Long assetId);

    List<TankCycle> findByStatus(CycleStatus status);

    @Query("SELECT tc FROM TankCycle tc WHERE tc.deadlineAt <= :deadline AND tc.status = :status")
    List<TankCycle> findOverdueOrWarningCycles(@Param("deadline") LocalDateTime deadline, @Param("status") CycleStatus status);

    @Query("SELECT tc FROM TankCycle tc WHERE tc.daysRemaining <= :days AND tc.status = :status")
    List<TankCycle> findCyclesNearDeadline(@Param("days") Integer days, @Param("status") CycleStatus status);

    boolean existsByPublicCode(String publicCode);
}
