package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import com.seneau.tankflow.data.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByCycleId(Long cycleId);

    List<Alert> findByStatus(String status);

    List<Alert> findByLevel(AlertLevel level);

    @Query("SELECT a FROM Alert a WHERE a.status = 'OPEN' ORDER BY a.level DESC, a.triggeredAt ASC")
    List<Alert> findAllOpenAlerts();

    @Query("SELECT a FROM Alert a WHERE a.cycleId = :cycleId AND a.status = 'OPEN'")
    List<Alert> findOpenAlertsByCycleId(@Param("cycleId") Long cycleId);
}
