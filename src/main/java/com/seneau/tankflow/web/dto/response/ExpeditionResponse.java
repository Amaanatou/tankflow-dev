package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpeditionResponse {

    private Long id;

    private String reference;

    private String type;

    private String origine;

    private String destination;

    private String statut;

    private LocalDateTime dateDepart;

    private LocalDateTime dateArrivee;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
