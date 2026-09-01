package com.seneau.tankflow.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCycleToExpeditionRequest {

    @NotNull(message = "Cycle ID is required")
    private Long cycleId;

    @NotNull(message = "Tank ID is required")
    private Long tankId;
}
