package com.seneau.tankflow.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableTankDetail {

    @JsonProperty("numeroFabricant")
    private String manufacturerSerial;

    private String reason;

    private Long activeCycleId;

    private String additionalInfo;
}
