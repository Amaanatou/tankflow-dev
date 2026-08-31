package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByCode(String code);

    List<Location> findByLocationType(String locationType);

    List<Location> findByIsActive(Boolean isActive);

    boolean existsByCode(String code);
}
