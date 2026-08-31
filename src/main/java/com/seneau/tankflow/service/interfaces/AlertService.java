package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import com.seneau.tankflow.data.model.Alert;

import java.util.List;

public interface AlertService {

    Alert createAlert(Long cycleId, AlertLevel level);

    List<Alert> getAlertsByCycle(Long cycleId);

    List<Alert> getOpenAlerts();

    List<Alert> getAlertsByLevel(AlertLevel level);

    Alert resolveAlert(Long alertId, Long resolvedByUserId, String notes);

    void checkAndCreateAlertsForCycle(Long cycleId);

    void checkAllOverdueCycles();

    long getOpenAlertCount();
}
