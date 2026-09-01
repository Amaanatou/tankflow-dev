package com.seneau.tankflow.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTankRequest {

    @NotBlank(message = "Manufacturer serial is required")
    private String manufacturerSerial;

    @NotBlank(message = "Supplier ID is required")
    private String supplierId;

    @NotNull(message = "Safety bell presence is required")
    private Boolean hasSafetyBell;

    private String tankStatus;

    private String notes;
}
