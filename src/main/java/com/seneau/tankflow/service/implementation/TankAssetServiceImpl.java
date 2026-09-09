package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.model.TankAsset;
import com.seneau.tankflow.data.repository.TankAssetRepository;
import com.seneau.tankflow.service.interfaces.TankAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TankAssetServiceImpl implements TankAssetService {

    private final TankAssetRepository tankAssetRepository;

    @Override
    public TankAsset createTank(String manufacturerSerial, String supplierId, Boolean hasSafetyBell, String tankStatus, String notes) {
        log.info("Creating tank: manufacturerSerial={}, supplierId={}", manufacturerSerial, supplierId);

        TankAsset tank = new TankAsset();
        tank.setManufacturerSerial(manufacturerSerial);
        tank.setSupplierId(supplierId);
        tank.setHasSafetyBell(hasSafetyBell);
        tank.setTankStatus(tankStatus);
        tank.setNotes(notes);

        return tankAssetRepository.save(tank);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankAsset> getTankById(Long id) {
        log.info("Fetching tank by ID: {}", id);
        return tankAssetRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankAsset> getTankByManufacturerSerial(String manufacturerSerial) {
        log.info("Fetching tank by manufacturer serial: {}", manufacturerSerial);
        return tankAssetRepository.findByManufacturerSerial(manufacturerSerial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankAsset> getAllTanks() {
        log.info("Fetching all tanks");
        return tankAssetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankAsset> findTanksByManufacturerSerials(List<String> manufacturerSerials) {
        log.info("Fetching tanks by manufacturer serials: {}", manufacturerSerials);
        return tankAssetRepository.findByManufacturerSerialIn(manufacturerSerials);
    }

    @Override
    public TankAsset updateTank(Long id, String supplierId, Boolean hasSafetyBell, String tankStatus, String notes) {
        log.info("Updating tank ID={}, supplierId={}", id, supplierId);

        TankAsset tank = tankAssetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tank not found with ID: " + id));

        tank.setSupplierId(supplierId);
        tank.setHasSafetyBell(hasSafetyBell);
        tank.setTankStatus(tankStatus);
        tank.setNotes(notes);

        return tankAssetRepository.save(tank);
    }

    @Override
    public void deleteTank(Long id) {
        log.info("Deleting tank ID={}", id);
        tankAssetRepository.deleteById(id);
    }
}
