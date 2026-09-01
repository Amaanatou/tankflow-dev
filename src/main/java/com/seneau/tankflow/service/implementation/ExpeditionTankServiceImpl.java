package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.model.ExpeditionTank;
import com.seneau.tankflow.data.repository.ExpeditionTankRepository;
import com.seneau.tankflow.service.interfaces.ExpeditionTankService;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
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
public class ExpeditionTankServiceImpl implements ExpeditionTankService {

    private final ExpeditionTankRepository expeditionTankRepository;

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
