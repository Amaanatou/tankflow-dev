package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seneau.tankflow.data.enumeration.ExpeditionStatus;
import com.seneau.tankflow.data.enumeration.ExpeditionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpeditionRequest {

    @NotBlank(message = "Reference is required")
    private String reference;

    @NotNull(message = "Type is required")
    private ExpeditionType type;

    @NotBlank(message = "Origine is required")
    private String origine;

    @NotBlank(message = "Destination is required")
    private String destination;

    private String transporteur;

    @NotNull(message = "Statut is required")
    private ExpeditionStatus statut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDepart;
}
