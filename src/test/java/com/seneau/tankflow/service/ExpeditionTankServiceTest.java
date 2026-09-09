package com.seneau.tankflow.service;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.Expedition;
import com.seneau.tankflow.data.model.TankAsset;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.repository.ExpeditionRepository;
import com.seneau.tankflow.data.repository.ExpeditionTankRepository;
import com.seneau.tankflow.service.implementation.ExpeditionTankServiceImpl;
import com.seneau.tankflow.service.interfaces.TankAssetService;
import com.seneau.tankflow.service.interfaces.TankCycleService;
import com.seneau.tankflow.web.dto.response.AddTanksToExpeditionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpeditionTankServiceTest {

    @Mock
    private ExpeditionTankRepository expeditionTankRepository;

    @Mock
    private ExpeditionRepository expeditionRepository;

    @Mock
    private TankAssetService tankAssetService;

    @Mock
    private TankCycleService tankCycleService;

    @InjectMocks
    private ExpeditionTankServiceImpl expeditionTankService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTanksBySerialNumbers_Success() {
        Long expeditionId = 1L;
        List<String> serials = Arrays.asList("TANK-001", "TANK-002");

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);

        TankAsset tank1 = new TankAsset();
        tank1.setId(1L);
        tank1.setManufacturerSerial("TANK-001");
        tank1.setSupplierId("SUP-A");
        tank1.setTankStatus("ACTIVE");

        TankAsset tank2 = new TankAsset();
        tank2.setId(2L);
        tank2.setManufacturerSerial("TANK-002");
        tank2.setSupplierId("SUP-B");
        tank2.setTankStatus("ACTIVE");

        TankCycle cycle1 = new TankCycle();
        cycle1.setId(1L);
        cycle1.setAssetId(tank1.getId());
        cycle1.setStatus(CycleStatus.IN_PROGRESS);

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(tankAssetService.getTankByManufacturerSerial("TANK-001")).thenReturn(Optional.of(tank1));
        when(tankAssetService.getTankByManufacturerSerial("TANK-002")).thenReturn(Optional.of(tank2));
        when(tankCycleService.getActiveCycleByAssetId(tank1.getId())).thenReturn(Optional.empty());
        when(tankCycleService.getActiveCycleByAssetId(tank2.getId())).thenReturn(Optional.empty());
        when(tankCycleService.createCycle(any(), any(), any())).thenReturn(cycle1);

        AddTanksToExpeditionResult result = expeditionTankService.addTanksBySerialNumbers(expeditionId, serials);

        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(2, result.getAdded().size());
        assertTrue(result.getAllSuccessful());
    }

    @Test
    void testAddTanksBySerialNumbers_WithDuplicates() {
        Long expeditionId = 1L;
        List<String> serials = Arrays.asList("TANK-001", "TANK-001", "TANK-002");

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);

        TankAsset tank = new TankAsset();
        tank.setId(1L);
        tank.setManufacturerSerial("TANK-001");
        tank.setSupplierId("SUP-A");

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(tankAssetService.getTankByManufacturerSerial("TANK-001")).thenReturn(Optional.of(tank));
        when(tankAssetService.getTankByManufacturerSerial("TANK-002")).thenReturn(Optional.empty());
        when(tankCycleService.getActiveCycleByAssetId(tank.getId())).thenReturn(Optional.empty());
        when(tankCycleService.createCycle(any(), any(), any())).thenReturn(new TankCycle());

        AddTanksToExpeditionResult result = expeditionTankService.addTanksBySerialNumbers(expeditionId, serials);

        assertTrue(result.getDuplicates().contains("TANK-001"));
        assertEquals(1, result.getDuplicates().size());
    }

    @Test
    void testAddTanksBySerialNumbers_WithActiveCycle() {
        Long expeditionId = 1L;
        List<String> serials = Arrays.asList("TANK-001");

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);

        TankAsset tank = new TankAsset();
        tank.setId(1L);
        tank.setManufacturerSerial("TANK-001");
        tank.setSupplierId("SUP-A");

        TankCycle activeCycle = new TankCycle();
        activeCycle.setId(1L);
        activeCycle.setStatus(CycleStatus.IN_PROGRESS);
        activeCycle.setPublicCode("TANK-001-2024-ABC123");

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(tankAssetService.getTankByManufacturerSerial("TANK-001")).thenReturn(Optional.of(tank));
        when(tankCycleService.getActiveCycleByAssetId(tank.getId())).thenReturn(Optional.of(activeCycle));

        AddTanksToExpeditionResult result = expeditionTankService.addTanksBySerialNumbers(expeditionId, serials);

        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getErrorCount());
        assertEquals(1, result.getUnavailable().size());
        assertEquals("CYCLE_IN_PROGRESS", result.getUnavailable().get(0).getReason());
        assertFalse(result.getAllSuccessful());
    }

    @Test
    void testAddTanksBySerialNumbers_TankNotFound() {
        Long expeditionId = 1L;
        List<String> serials = Arrays.asList("TANK-UNKNOWN");

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(tankAssetService.getTankByManufacturerSerial("TANK-UNKNOWN")).thenReturn(Optional.empty());

        AddTanksToExpeditionResult result = expeditionTankService.addTanksBySerialNumbers(expeditionId, serials);

        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getErrorCount());
        assertEquals(1, result.getNotFound().size());
        assertTrue(result.getNotFound().contains("TANK-UNKNOWN"));
        assertFalse(result.getAllSuccessful());
    }
}
