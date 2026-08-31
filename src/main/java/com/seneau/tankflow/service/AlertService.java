package com.seneau.tankflow.service;

import com.seneau.tankflow.data.enumeration.AlertLevel;
import com.seneau.tankflow.data.model.Alert;
import com.seneau.tankflow.web.dto.response.AlertResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des alertes.
 * Génère automatiquement les alertes selon les seuils.
 * 4 niveaux: J-15 (WARNING), J-10 (URGENT), J-5 (CRITICAL), J+0 (OVERDUE)
 */
public interface AlertService {

    /**
     * Crée une alerte pour un cycle.
     */
    AlertResponse createAlert(Long cycleId, AlertLevel level);

    /**
     * Récupère toutes les alertes ouvertes.
     */
    List<AlertResponse> getAllOpenAlerts();

    /**
     * Récupère les alertes ouvertes pour un cycle.
     */
    List<AlertResponse> getOpenAlertsByCycleId(Long cycleId);

    /**
     * Récupère toutes les alertes d'un cycle.
     */
    List<AlertResponse> getAlertsByCycleId(Long cycleId);

    /**
     * Résout une alerte.
     */
    AlertResponse resolveAlert(Long alertId, Long resolvedByUserId, String notes);

    /**
     * Calcule automatiquement les alertes pour un cycle.
     * Appelé périodiquement (toutes les 15 minutes).
     */
    void calculateAndCreateAlertsForAllCycles();

    /**
     * Vérifie s'il y a une alerte ouverte à ce niveau pour ce cycle.
     */
    boolean hasOpenAlert(Long cycleId, AlertLevel level);
}
