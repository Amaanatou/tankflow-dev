package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpeditionTankResponse {

    private Long id;

    private Long expeditionId;

    private Long cycleId;

    private Long tankId;

    private Boolean selected;

    private LocalDateTime createdAt;
}
