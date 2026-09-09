package com.seneau.tankflow.service;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import com.seneau.tankflow.data.model.WorkflowEvent;
import com.seneau.tankflow.data.repository.TankCycleRepository;
import com.seneau.tankflow.data.repository.WorkflowEventRepository;
import com.seneau.tankflow.service.implementation.TankCycleServiceImpl;
import com.seneau.tankflow.web.dto.response.StepDurationDetail;
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
import static org.mockito.Mockito.*;

class TankCycleServiceTest {

    @Mock
    private TankCycleRepository tankCycleRepository;

    @Mock
    private WorkflowEventRepository workflowEventRepository;

    @InjectMocks
    private TankCycleServiceImpl tankCycleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateStepDurations_WithCompletedSteps() {
        Long cycleId = 1L;
        TankCycle cycle = new TankCycle();
        cycle.setId(cycleId);
        cycle.setCurrentStepNumber(3);
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        LocalDateTime step1Start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime step2Start = LocalDateTime.of(2024, 1, 5, 14, 30);
        LocalDateTime step3Start = LocalDateTime.of(2024, 1, 8, 9, 0);

        WorkflowEvent event1 = new WorkflowEvent();
        event1.setCycleId(cycleId);
        event1.setStepNumber(1);
        event1.setEventTimestamp(step1Start);

        WorkflowEvent event2 = new WorkflowEvent();
        event2.setCycleId(cycleId);
        event2.setStepNumber(2);
        event2.setEventTimestamp(step2Start);

        WorkflowEvent event3 = new WorkflowEvent();
        event3.setCycleId(cycleId);
        event3.setStepNumber(3);
        event3.setEventTimestamp(step3Start);

        when(tankCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));
        when(workflowEventRepository.findByCycleIdOrderByCreatedAtAsc(cycleId))
                .thenReturn(Arrays.asList(event1, event2, event3));

        List<StepDurationDetail> result = tankCycleService.calculateStepDurations(cycleId);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().anyMatch(s -> s.getStepNumber() == 1));
        assertTrue(result.stream().anyMatch(s -> s.getStepLabel().contains("Expédition")));
    }

    @Test
    void testCalculateStepDurations_EmptyEvents() {
        Long cycleId = 1L;
        TankCycle cycle = new TankCycle();
        cycle.setId(cycleId);

        when(tankCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));
        when(workflowEventRepository.findByCycleIdOrderByCreatedAtAsc(cycleId))
                .thenReturn(Arrays.asList());

        List<StepDurationDetail> result = tankCycleService.calculateStepDurations(cycleId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetActiveCycleByAssetId_Found() {
        Long assetId = 1L;
        TankCycle activeCycle = new TankCycle();
        activeCycle.setId(1L);
        activeCycle.setAssetId(assetId);
        activeCycle.setStatus(CycleStatus.IN_PROGRESS);

        when(tankCycleRepository.findActiveCyclesByAssetId(assetId))
                .thenReturn(Arrays.asList(activeCycle));

        Optional<TankCycle> result = tankCycleService.getActiveCycleByAssetId(assetId);

        assertTrue(result.isPresent());
        assertEquals(CycleStatus.IN_PROGRESS, result.get().getStatus());
    }

    @Test
    void testGetActiveCycleByAssetId_NotFound() {
        Long assetId = 1L;

        when(tankCycleRepository.findActiveCyclesByAssetId(assetId))
                .thenReturn(Arrays.asList());

        Optional<TankCycle> result = tankCycleService.getActiveCycleByAssetId(assetId);

        assertFalse(result.isPresent());
    }

    @Test
    void testStepDurationDetail_DurationCalculation() {
        Long cycleId = 1L;
        TankCycle cycle = new TankCycle();
        cycle.setId(cycleId);
        cycle.setCurrentStepNumber(1);
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 6, 0, 0);

        WorkflowEvent event1 = new WorkflowEvent();
        event1.setCycleId(cycleId);
        event1.setStepNumber(1);
        event1.setEventTimestamp(start);

        WorkflowEvent event2 = new WorkflowEvent();
        event2.setCycleId(cycleId);
        event2.setStepNumber(1);
        event2.setEventTimestamp(end);

        when(tankCycleRepository.findById(cycleId)).thenReturn(Optional.of(cycle));
        when(workflowEventRepository.findByCycleIdOrderByCreatedAtAsc(cycleId))
                .thenReturn(Arrays.asList(event1, event2));

        List<StepDurationDetail> result = tankCycleService.calculateStepDurations(cycleId);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        StepDurationDetail detail = result.get(0);
        assertEquals(1, detail.getStepNumber());
        assertEquals(5.0, detail.getDurationDays(), 0.1);
        assertEquals("COMPLETED", detail.getStatus());
    }
}
