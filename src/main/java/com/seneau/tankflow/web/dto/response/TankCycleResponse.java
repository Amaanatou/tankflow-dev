package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankCycleResponse {

    private Long id;

    private String publicCode;

    private Long assetId;

    private LocalDateTime startedAt;

    private LocalDateTime returnedToSupplierAt;

    private LocalDateTime deadlineAt;

    private String status;

    private Integer currentStepNumber;

    private Integer durationDays;

    private Integer daysRemaining;

    private BigDecimal penaltyAmount;

    private Boolean isPenaltyApplied;

    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
