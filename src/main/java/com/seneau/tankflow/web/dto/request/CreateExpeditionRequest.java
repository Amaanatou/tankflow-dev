package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Origine is required")
    private String origine;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Statut is required")
    private String statut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDepart;  // optionnel, défaut NOW()
}
