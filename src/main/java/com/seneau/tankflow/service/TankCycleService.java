package com.seneau.tankflow.service;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.web.dto.request.CreateCycleRequest;
import com.seneau.tankflow.web.dto.response.CycleResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des cycles de tanks.
 * Cas d'utilisation: créer, modifier, rechercher, clôturer les cycles.
 */
public interface TankCycleService {

    /**
     * Crée un nouveau cycle pour un tank.
     * Génère automatiquement l'ID unique (TANK-[N°fab]-[Année]-[CodeAléatoire])
     * Initialise la deadline à 180 jours.
     */
    CycleResponse createCycle(CreateCycleRequest request);

    /**
     * Récupère un cycle par son ID public.
     */
    Optional<CycleResponse> getCycleByPublicCode(String publicCode);

    /**
     * Récupère tous les cycles d'un tank physique.
     */
    List<CycleResponse> getCyclesByAssetId(Long assetId);

    /**
     * Récupère tous les cycles avec un statut donné.
     */
    List<CycleResponse> getCyclesByStatus(CycleStatus status);

    /**
     * Récupère les cycles qui approchent de la deadline (J-15, J-10, J-5).
     */
    List<CycleResponse> getNearDeadlineCycles(Integer daysThreshold);

    /**
     * Avance un cycle à l'étape suivante.
     * Valide les transitions selon la règle métier.
     */
    CycleResponse advanceStep(Long cycleId, Integer nextStepNumber);

    /**
     * Clôt un cycle et calcule la pénalité si dépassement.
     */
    CycleResponse closeCycle(Long cycleId);

    /**
     * Calcule le nombre de jours restants avant deadline.
     */
    Integer calculateDaysRemaining(Long cycleId);

    /**
     * Calcule la pénalité si le cycle dépasse 180 jours.
     * 3€ par tank par jour de dépassement.
     */
    java.math.BigDecimal calculatePenalty(Long cycleId);
}
