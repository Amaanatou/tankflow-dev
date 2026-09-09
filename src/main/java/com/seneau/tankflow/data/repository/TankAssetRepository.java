package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.TankAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TankAssetRepository extends JpaRepository<TankAsset, Long> {

    Optional<TankAsset> findByManufacturerSerial(String manufacturerSerial);

    boolean existsByManufacturerSerial(String manufacturerSerial);

    List<TankAsset> findByManufacturerSerialIn(List<String> manufacturerSerials);
}
