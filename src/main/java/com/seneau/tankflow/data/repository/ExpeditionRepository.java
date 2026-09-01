package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.Expedition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, Long> {

    Optional<Expedition> findByReference(String reference);

    List<Expedition> findByStatut(String statut);

    List<Expedition> findByOrigine(String origine);

    List<Expedition> findByDestination(String destination);

    boolean existsByReference(String reference);
}
