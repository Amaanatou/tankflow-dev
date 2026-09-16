package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer un nouveau cycle.
 * Reçoit l'ID du tank (asset) et crée automatiquement le cycle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCycleRequest {

    @JsonProperty("tankId")
    @NotNull(message = "Tank ID is required")
    private Long assetId;
}
