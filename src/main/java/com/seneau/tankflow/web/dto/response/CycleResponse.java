package com.seneau.tankflow.web.dto.response;

import com.seneau.tankflow.data.enumeration.CycleStatus;
import com.seneau.tankflow.data.model.TankCycle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un cycle.
 * Expose les informations publiques d'un cycle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleResponse {

    private Long id;
    private String publicCode;  // Format: TANK-[N°fab]-[Année]-[Code]
    private Long assetId;
    private LocalDateTime startedAt;
    private LocalDateTime returnedToSupplierAt;
    private LocalDateTime deadlineAt;
    private CycleStatus status;
    private Integer currentStepNumber;  // 1-9
    private Integer durationDays;
    private Integer daysRemaining;
    private BigDecimal penaltyAmount;
    private Boolean isPenaltyApplied;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Crée un DTO à partir d'une entité.
     */
    public static CycleResponse from(TankCycle cycle) {
        return new CycleResponse(
                cycle.getId(),
                cycle.getPublicCode(),
                cycle.getAssetId(),
                cycle.getStartedAt(),
                cycle.getReturnedToSupplierAt(),
                cycle.getDeadlineAt(),
                cycle.getStatus(),
                cycle.getCurrentStepNumber(),
                cycle.getDurationDays(),
                cycle.getDaysRemaining(),
                cycle.getPenaltyAmount(),
                cycle.getIsPenaltyApplied(),
                cycle.getCreatedAt(),
                cycle.getUpdatedAt()
        );
    }
}
