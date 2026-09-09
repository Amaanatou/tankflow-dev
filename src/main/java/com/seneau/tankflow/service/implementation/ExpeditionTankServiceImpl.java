package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.model.ExpeditionTank;
import com.seneau.tankflow.data.model.TankAsset;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.ExpeditionRepository;
import com.seneau.tankflow.data.repository.ExpeditionTankRepository;
import com.seneau.tankflow.service.interfaces.ExpeditionTankService;
import com.seneau.tankflow.service.interfaces.TankAssetService;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import com.seneau.tankflow.web.dto.response.AddTanksToExpeditionResult;
import com.seneau.tankflow.web.dto.response.TankProcessingResultDetail;
import com.seneau.tankflow.web.dto.response.UnavailableTankDetail;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpeditionTankServiceImpl implements ExpeditionTankService {

    private final ExpeditionTankRepository expeditionTankRepository;
    private final ExpeditionRepository expeditionRepository;
    private final TankAssetService tankAssetService;
    private final TankCycleService tankCycleService;

    @Override
    public ExpeditionTank addCycleToExpedition(Long expeditionId, Long cycleId, Long tankId) {
        log.info("Adding cycle {} (tank {}) to expedition {}", cycleId, tankId, expeditionId);

        if (expeditionTankRepository.existsByExpeditionIdAndCycleId(expeditionId, cycleId)) {
            throw new IllegalArgumentException("Cycle already in expedition");
        }

        ExpeditionTank expeditionTank = new ExpeditionTank();
        expeditionTank.setExpeditionId(expeditionId);
        expeditionTank.setCycleId(cycleId);
        expeditionTank.setTankId(tankId);
        expeditionTank.setSelected(false);

        return expeditionTankRepository.save(expeditionTank);
    }

    @Override
    public AddTanksToExpeditionResult addTanksBySerialNumbers(Long expeditionId, List<String> manufacturerSerials) {
        log.info("Adding tanks by serial numbers to expedition {}: {}", expeditionId, manufacturerSerials);

        expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        AddTanksToExpeditionResult result = AddTanksToExpeditionResult.empty();
        Set<String> processedSerials = new HashSet<>();
        Set<String> duplicatesFound = new HashSet<>();

        for (String serial : manufacturerSerials) {
            if (processedSerials.contains(serial)) {
                duplicatesFound.add(serial);
                log.warn("Duplicate tank serial in request: {}", serial);
                continue;
            }
            processedSerials.add(serial);

            Optional<TankAsset> tankOpt = tankAssetService.getTankByManufacturerSerial(serial);

            if (tankOpt.isEmpty()) {
                result.getNotFound().add(serial);
                result.setErrorCount(result.getErrorCount() + 1);
                log.warn("Tank not found: {}", serial);
                continue;
            }

            TankAsset tank = tankOpt.get();
            Optional<TankCycle> activeCycle = tankCycleService.getActiveCycleByAssetId(tank.getId());

            if (activeCycle.isPresent()) {
                UnavailableTankDetail unavailable = new UnavailableTankDetail();
                unavailable.setManufacturerSerial(serial);
                unavailable.setReason("CYCLE_IN_PROGRESS");
                unavailable.setActiveCycleId(activeCycle.get().getId());
                unavailable.setAdditionalInfo("Tank has active cycle: " + activeCycle.get().getPublicCode());
                result.getUnavailable().add(unavailable);
                result.setErrorCount(result.getErrorCount() + 1);
                log.warn("Tank has active cycle: {} ({})", serial, activeCycle.get().getId());
                continue;
            }

            try {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime deadline = now.plusDays(210);
                TankCycle newCycle = tankCycleService.createCycle(tank.getId(), now, deadline);

                ExpeditionTank expeditionTank = addCycleToExpedition(expeditionId, newCycle.getId(), tank.getId());

                TankProcessingResultDetail detail = new TankProcessingResultDetail();
                detail.setTankId(tank.getId());
                detail.setManufacturerSerial(tank.getManufacturerSerial());
                detail.setSupplierId(tank.getSupplierId());
                detail.setTankStatus(tank.getTankStatus());
                result.getAdded().add(detail);
                result.setSuccessCount(result.getSuccessCount() + 1);
                log.info("Tank added successfully: {} (cycle {})", serial, newCycle.getId());

            } catch (Exception e) {
                UnavailableTankDetail error = new UnavailableTankDetail();
                error.setManufacturerSerial(serial);
                error.setReason("PROCESSING_ERROR");
                error.setAdditionalInfo(e.getMessage());
                result.getUnavailable().add(error);
                result.setErrorCount(result.getErrorCount() + 1);
                log.error("Error processing tank: {}", serial, e);
            }
        }

        result.setDuplicates(new ArrayList<>(duplicatesFound));
        result.setTotalProcessed(processedSerials.size());
        result.setAllSuccessful(result.getErrorCount() == 0 && duplicatesFound.isEmpty());

        log.info("Tank processing completed: added={}, unavailable={}, notFound={}, duplicates={}",
                result.getAdded().size(), result.getUnavailable().size(),
                result.getNotFound().size(), result.getDuplicates().size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpeditionTank> getExpeditionTankById(Long id) {
        log.debug("Fetching expedition-tank by ID: {}", id);
        return expeditionTankRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpeditionTank> getCyclesByExpedition(Long expeditionId) {
        log.debug("Fetching cycles for expedition {}", expeditionId);
        return expeditionTankRepository.findByExpeditionId(expeditionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpeditionTank> getExpeditionsByCycle(Long cycleId) {
        log.debug("Fetching expeditions for cycle {}", cycleId);
        return expeditionTankRepository.findByCycleId(cycleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpeditionTank> getExpeditionsByTank(Long tankId) {
        log.debug("Fetching expeditions for tank {}", tankId);
        return expeditionTankRepository.findByTankId(tankId);
    }

    @Override
    public ExpeditionTank markAsSelected(Long id) {
        log.info("Marking expedition-tank {} as selected", id);

        ExpeditionTank expeditionTank = expeditionTankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpeditionTank not found with ID: " + id));

        expeditionTank.setSelected(true);

        return expeditionTankRepository.save(expeditionTank);
    }

    @Override
    public void removeCycleFromExpedition(Long id) {
        log.info("Removing cycle from expedition (ID: {})", id);

        if (!expeditionTankRepository.existsById(id)) {
            throw new ResourceNotFoundException("ExpeditionTank not found with ID: " + id);
        }

        expeditionTankRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCyclesInExpedition(Long expeditionId) {
        log.debug("Counting cycles in expedition {}", expeditionId);
        return expeditionTankRepository.countByExpeditionId(expeditionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalExpeditionTankCount() {
        long count = expeditionTankRepository.count();
        log.debug("Total expedition-tank count: {}", count);
        return count;
    }
}
