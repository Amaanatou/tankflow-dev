package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTankRequest {

    @JsonProperty("numeroFabricant")
    @NotBlank(message = "Manufacturer serial is required")
    private String manufacturerSerial;

    @JsonProperty("fournisseur")
    @NotBlank(message = "Supplier is required")
    private String supplierId;

    @NotNull(message = "Safety bell presence is required")
    private Boolean hasSafetyBell;

    @JsonProperty("statut")
    private String tankStatus;

    private String notes;
}
