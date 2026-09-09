package com.seneau.tankflow.service;

import com.seneau.tankflow.data.model.Expedition;
import com.seneau.tankflow.data.repository.ExpeditionRepository;
import com.seneau.tankflow.service.implementation.ExpeditionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpeditionServiceTest {

    @Mock
    private ExpeditionRepository expeditionRepository;

    @InjectMocks
    private ExpeditionServiceImpl expeditionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExpedition_WithoutDateDepart() {
        when(expeditionRepository.existsByReference("EXP-001")).thenReturn(false);
        when(expeditionRepository.save(any())).thenAnswer(invocation -> {
            Expedition exp = invocation.getArgument(0);
            exp.setId(1L);
            return exp;
        });

        Expedition result = expeditionService.createExpedition(
                "EXP-001", "OUTBOUND", "Paris", "Lyon", "SENT"
        );

        assertNotNull(result);
        assertNotNull(result.getDateDepart());
        verify(expeditionRepository).save(any());
    }

    @Test
    void testCreateExpedition_WithDateDepart() {
        LocalDateTime customDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        Expedition expedition = new Expedition();
        expedition.setId(1L);
        expedition.setReference("EXP-001");
        expedition.setDateDepart(customDate);

        when(expeditionRepository.existsByReference("EXP-001")).thenReturn(false);
        when(expeditionRepository.save(any())).thenReturn(expedition);

        Expedition result = expeditionService.createExpedition(
                "EXP-001", "OUTBOUND", "Paris", "Lyon", "SENT", customDate
        );

        assertNotNull(result);
        assertEquals(customDate, result.getDateDepart());
    }

    @Test
    void testUpdateExpeditionArrival_WithFlexibleDate() {
        Long expeditionId = 1L;
        LocalDateTime departDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime arrivalDate = LocalDateTime.of(2024, 1, 5, 14, 30);

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);
        expedition.setDateDepart(departDate);
        expedition.setStatut("SENT");

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(expeditionRepository.save(any())).thenReturn(expedition);

        Expedition result = expeditionService.updateExpeditionArrival(expeditionId, arrivalDate);

        assertNotNull(result);
        assertEquals(arrivalDate, result.getDateArrivee());
        assertEquals("LIVRÉE", result.getStatut());
    }

    @Test
    void testUpdateExpeditionArrival_WithPastDate() {
        Long expeditionId = 1L;
        LocalDateTime pastDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime arrivalDate = LocalDateTime.of(2023, 12, 31, 14, 30);

        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);
        expedition.setDateDepart(pastDate);

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));

        assertThrows(IllegalArgumentException.class, () -> {
            expeditionService.updateExpeditionArrival(expeditionId, arrivalDate);
        });
    }

    @Test
    void testUpdateExpeditionArrival_DefaultToNow() {
        Long expeditionId = 1L;
        Expedition expedition = new Expedition();
        expedition.setId(expeditionId);
        expedition.setDateDepart(LocalDateTime.now().minusDays(1));

        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(expeditionRepository.save(any())).thenReturn(expedition);

        expeditionService.updateExpeditionArrival(expeditionId);

        assertNotNull(expedition.getDateArrivee());
    }
}
