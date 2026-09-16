package com.seneau.tankflow.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTanksBySerialRequest {

    @JsonProperty("numerosFabricants")
    @NotEmpty(message = "Manufacturer serials list cannot be empty")
    private List<String> manufacturerSerials;
}
