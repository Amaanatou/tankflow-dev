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
        log.info("Creating expedition: {} from {} to {}", reference, origine, destination);

        if (expeditionRepository.existsByReference(reference)) {
            throw new IllegalArgumentException("Expedition reference already exists: " + reference);
        }

        Expedition expedition = new Expedition();
        expedition.setReference(reference);
        expedition.setType(type);
        expedition.setOrigine(origine);
        expedition.setDestination(destination);
        expedition.setStatut(statut);
        expedition.setDateDepart(LocalDateTime.now());

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

        expedition.setStatut(newStatut);
        expedition.setUpdatedAt(LocalDateTime.now());

        return expeditionRepository.save(expedition);
    }

    @Override
    public Expedition updateExpeditionArrival(Long id) {
        log.info("Marking expedition {} as arrived", id);

        Expedition expedition = expeditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + id));

        expedition.setDateArrivee(LocalDateTime.now());
        expedition.setStatut("LIVRÉE");
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
