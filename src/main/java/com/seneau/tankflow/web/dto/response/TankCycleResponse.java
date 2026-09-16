package com.seneau.tankflow.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("codePublic")
    private String publicCode;

    @JsonProperty("tankId")
    private Long assetId;

    @JsonProperty("dateDebut")
    private LocalDateTime startedAt;

    @JsonProperty("dateFin")
    private LocalDateTime returnedToSupplierAt;

    @JsonProperty("dateLimite")
    private LocalDateTime deadlineAt;

    @JsonProperty("statut")
    private String status;

    @JsonProperty("etapeActuelle")
    private Integer currentStepNumber;

    @JsonProperty("joursEcoules")
    private Integer durationDays;

    @JsonProperty("joursRestants")
    private Integer daysRemaining;

    @JsonProperty("niveauAlerte")
    private String alertLevel;

    private String localisation;

    private String zone;

    private BigDecimal penaltyAmount;

    private Boolean isPenaltyApplied;

    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
