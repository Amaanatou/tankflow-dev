package com.seneau.tankflow.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Localisation physique (site, usine, port, etc.).
 * Contient des zones de stockage.
 */
@Entity
@Table(name = "location", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Location extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Code court (MAIN_SITE, FACTORY_A, etc.)

    @Column(nullable = false, length = 255)
    private String name;  // Nom complet

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String locationType;  // MAIN_WAREHOUSE, FACTORY, PORT, SUPPLIER

    @Column(nullable = false)
    private Boolean isActive;  // Lieu actif
}
