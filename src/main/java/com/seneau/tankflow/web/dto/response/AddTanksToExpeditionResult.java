package com.seneau.tankflow.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTanksToExpeditionResult {

    private List<TankProcessingResultDetail> added;
    private List<UnavailableTankDetail> unavailable;
    private List<String> notFound;
    private List<String> duplicates;

    private Integer totalProcessed;
    private Integer successCount;
    private Integer errorCount;
    private Boolean allSuccessful;

    public static AddTanksToExpeditionResult empty() {
        return AddTanksToExpeditionResult.builder()
                .added(new ArrayList<>())
                .unavailable(new ArrayList<>())
                .notFound(new ArrayList<>())
                .duplicates(new ArrayList<>())
                .totalProcessed(0)
                .successCount(0)
                .errorCount(0)
                .allSuccessful(true)
                .build();
    }
}
