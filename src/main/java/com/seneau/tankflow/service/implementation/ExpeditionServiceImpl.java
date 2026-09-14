package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.model.Expedition;
import com.seneau.tankflow.data.repository.ExpeditionRepository;
import com.seneau.tankflow.service.interfaces.ExpeditionService;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpeditionServiceImpl implements ExpeditionService {

    private final ExpeditionRepository expeditionRepository;

    @Override
    public Expedition createExpedition(String reference, String type, String origine, String destination, String statut) {
        return createExpedition(reference, type, origine, destination, statut, LocalDateTime.now());
    }

    @Override
    public Expedition createExpedition(String reference, String type, String origine, String destination, String statut, LocalDateTime dateDepart) {
        log.info("Creating expedition: {} from {} to {}", reference, origine, destination);

        if (expeditionRepository.existsByReference(reference)) {
            throw new IllegalArgumentException("Expedition reference already exists: " + reference);
        }

        Expedition expedition = new Expedition();
        expedition.setReference(reference);
        expedition.setOrigine(origine);
        expedition.setDestination(destination);
        expedition.setDateDepart(dateDepart != null ? dateDepart : LocalDateTime.now());

        return expeditionRepository.save(expedition);
    }

    @Override
    public Expedition createExpeditionWithEnums(String reference, com.seneau.tankflow.data.enumeration.ExpeditionType type,
                                               String origine, String destination, String transporteur,
                                               com.seneau.tankflow.data.enumeration.ExpeditionStatus statut,
                                               LocalDateTime dateDepart) {
        log.info("Creating expedition: {} from {} to {}", reference, origine, destination);

        if (expeditionRepository.existsByReference(reference)) {
            throw new IllegalArgumentException("Expedition reference already exists: " + reference);
        }

        Expedition expedition = new Expedition();
        expedition.setReference(reference);
        expedition.setType(type);
        expedition.setOrigine(origine);
        expedition.setDestination(destination);
        expedition.setTransporteur(transporteur);
        expedition.setStatut(statut);
        expedition.setDateDepart(dateDepart != null ? dateDepart : LocalDateTime.now());

        return expeditionRepository.save(expedition);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Expedition> getExpeditionById(Long id) {
        log.debug("Fetching expedition by ID: {}", id);
        return expeditionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Expedition> getExpeditionByReference(String reference) {
        log.debug("Fetching expedition by reference: {}", reference);
        return expeditionRepository.findByReference(reference);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expedition> getExpeditionsByStatut(String statut) {
        log.debug("Fetching expeditions by statut: {}", statut);
        return expeditionRepository.findByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expedition> getExpeditionsByOrigine(String origine) {
        log.debug("Fetching expeditions from: {}", origine);
        return expeditionRepository.findByOrigine(origine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expedition> getExpeditionsByDestination(String destination) {
        log.debug("Fetching expeditions to: {}", destination);
        return expeditionRepository.findByDestination(destination);
    }

    @Override
    public Expedition updateExpeditionStatut(Long id, String newStatut) {
        log.info("Updating expedition {} statut to: {}", id, newStatut);

        Expedition expedition = expeditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + id));

        try {
            expedition.setStatut(com.seneau.tankflow.data.enumeration.ExpeditionStatus.valueOf(newStatut.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid statut value: {}, ignoring", newStatut);
        }
        expedition.setUpdatedAt(LocalDateTime.now());

        return expeditionRepository.save(expedition);
    }

    @Override
    public Expedition updateExpeditionArrival(Long id) {
        return updateExpeditionArrival(id, LocalDateTime.now());
    }

    @Override
    public Expedition updateExpeditionArrival(Long id, LocalDateTime dateArrivee) {
        log.info("Marking expedition {} as arrived at {}", id, dateArrivee);

        Expedition expedition = expeditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + id));

        if (dateArrivee.isBefore(expedition.getDateDepart())) {
            throw new IllegalArgumentException("dateArrivee cannot be before dateDepart");
        }

        expedition.setDateArrivee(dateArrivee);
        expedition.setStatut(com.seneau.tankflow.data.enumeration.ExpeditionStatus.RECEIVED);
        expedition.setUpdatedAt(LocalDateTime.now());

        return expeditionRepository.save(expedition);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalExpeditionCount() {
        long count = expeditionRepository.count();
        log.debug("Total expedition count: {}", count);
        return count;
    }
}
