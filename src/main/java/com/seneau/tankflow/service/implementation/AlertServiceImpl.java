package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.Alert;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.AlertRepository;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.service.AlertService;
import com.seneau.tankflow.web.dto.response.AlertResponse;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final TankCycleRepository cycleRepository;

    @Override
    public AlertResponse createAlert(Long cycleId, AlertLevel level) {
        log.info("Creating alert for cycle: {} at level: {}", cycleId, level);

        TankCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        // Vérifier si une alerte à ce niveau existe déjà
        if (hasOpenAlert(cycleId, level)) {
            log.debug("Alert already exists for cycle {} at level {}", cycleId, level);
            return null;  // Ne pas créer de doublon
        }

        Alert alert = new Alert();
        alert.setCycleId(cycleId);
        alert.setLevel(level);
        alert.setStatus("OPEN");
        alert.setThresholdDays(level.getDaysThreshold());
        alert.setTriggeredAt(LocalDateTime.now());

        Alert saved = alertRepository.save(alert);
        log.info("Alert created for cycle {}", cycleId);

        return AlertResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAllOpenAlerts() {
        return alertRepository.findAllOpenAlerts().stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getOpenAlertsByCycleId(Long cycleId) {
        return alertRepository.findOpenAlertsByCycleId(cycleId).stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByCycleId(Long cycleId) {
        return alertRepository.findByCycleId(cycleId).stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Override
    public AlertResponse resolveAlert(Long alertId, Long resolvedByUserId, String notes) {
        log.info("Resolving alert: {}", alertId);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        alert.setStatus("RESOLVED");
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedByUserId(resolvedByUserId);
        alert.setNotes(notes);

        Alert updated = alertRepository.save(alert);
        log.info("Alert resolved: {}", alertId);

        return AlertResponse.from(updated);
    }

    @Override
    @Scheduled(fixedDelay = 900000)  // 15 minutes
    public void calculateAndCreateAlertsForAllCycles() {
        log.info("Calculating alerts for all active cycles...");

        List<TankCycle> activeCycles = cycleRepository.findByStatus(CycleStatus.IN_PROGRESS);

        for (TankCycle cycle : activeCycles) {
            checkAndCreateAlertsForCycle(cycle);
        }

        log.info("Alert calculation completed for {} cycles", activeCycles.size());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOpenAlert(Long cycleId, AlertLevel level) {
        List<Alert> openAlerts = alertRepository.findOpenAlertsByCycleId(cycleId);
        return openAlerts.stream()
                .anyMatch(alert -> alert.getLevel() == level);
    }

    // Utilitaire privé
    private void checkAndCreateAlertsForCycle(TankCycle cycle) {
        int daysRemaining = calculateDaysRemaining(cycle);

        // J-15: WARNING
        if (daysRemaining == 15 && !hasOpenAlert(cycle.getId(), AlertLevel.WARNING)) {
            createAlert(cycle.getId(), AlertLevel.WARNING);
        }

        // J-10: URGENT
        if (daysRemaining == 10 && !hasOpenAlert(cycle.getId(), AlertLevel.URGENT)) {
            createAlert(cycle.getId(), AlertLevel.URGENT);
        }

        // J-5: CRITICAL
        if (daysRemaining == 5 && !hasOpenAlert(cycle.getId(), AlertLevel.CRITICAL)) {
            createAlert(cycle.getId(), AlertLevel.CRITICAL);
        }

        // J+0: OVERDUE
        if (daysRemaining <= 0 && !hasOpenAlert(cycle.getId(), AlertLevel.OVERDUE)) {
            createAlert(cycle.getId(), AlertLevel.OVERDUE);
        }
    }

    private int calculateDaysRemaining(TankCycle cycle) {
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS
                .between(LocalDateTime.now(), cycle.getDeadlineAt());
        return Math.max((int) daysRemaining, 0);
    }
}
