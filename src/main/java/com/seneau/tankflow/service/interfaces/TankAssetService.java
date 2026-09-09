package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.model.TankAsset;
import java.util.List;
import java.util.Optional;

public interface TankAssetService {
    TankAsset createTank(String manufacturerSerial, String supplierId, Boolean hasSafetyBell, String tankStatus, String notes);
    Optional<TankAsset> getTankById(Long id);
    Optional<TankAsset> getTankByManufacturerSerial(String manufacturerSerial);
    List<TankAsset> getAllTanks();
    List<TankAsset> findTanksByManufacturerSerials(List<String> manufacturerSerials);
    TankAsset updateTank(Long id, String supplierId, Boolean hasSafetyBell, String tankStatus, String notes);
    void deleteTank(Long id);
}
