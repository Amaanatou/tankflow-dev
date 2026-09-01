package com.seneau.tankflow.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ExpeditionTank - Table associative.
 * Représente l'inclusion d'un cycle de tank dans une expédition.
 * Une expédition peut contenir plusieurs cycles.
 */
@Entity
@Table(name = "expedition_tank", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"expedition_id", "cycle_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpeditionTank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long expeditionId;

    @Column(nullable = false)
    private Long cycleId;

    @Column(nullable = false)
    private Long tankId;

    @Column(nullable = false)
    private Boolean selected = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
