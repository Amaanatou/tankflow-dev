package com.seneau.tankflow.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Identité permanente du tank physique.
 * Un tank physique peut avoir plusieurs cycles (rotations).
 */
@Entity
@Table(name = "tank", uniqueConstraints = {
        @UniqueConstraint(columnNames = "manufacturer_serial")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TankAsset extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String manufacturerSerial;  // Numéro du fabricant (PERMANENT)

    @Column(nullable = false, length = 100)
    private String supplierId;  // ID fournisseur

    @Column(nullable = false)
    private Boolean hasSafetyBell;  // Cloche de sécurité présente

    @Column(length = 50)
    private String tankStatus;  // ACTIVE, INACTIVE, MAINTENANCE

    @Column(columnDefinition = "TEXT")
    private String notes;  // Observations libres
}
