package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.ExpeditionStatus;
import com.seneau.tankflow.data.enumeration.ExpeditionType;
import com.seneau.tankflow.data.model.Expedition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpeditionService {

    Expedition createExpedition(String reference, String type, String origine, String destination, String statut);

    Expedition createExpedition(String reference, String type, String origine, String destination, String statut, LocalDateTime dateDepart);

    Expedition createExpeditionWithEnums(String reference, ExpeditionType type, String origine, String destination, String transporteur, ExpeditionStatus statut, LocalDateTime dateDepart);

    Optional<Expedition> getExpeditionById(Long id);

    Optional<Expedition> getExpeditionByReference(String reference);

    List<Expedition> getExpeditionsByStatut(String statut);

    List<Expedition> getExpeditionsByOrigine(String origine);

    List<Expedition> getExpeditionsByDestination(String destination);

    Expedition updateExpeditionStatut(Long id, String newStatut);

    Expedition updateExpeditionArrival(Long id);

    Expedition updateExpeditionArrival(Long id, LocalDateTime dateArrivee);

    long getTotalExpeditionCount();
}
