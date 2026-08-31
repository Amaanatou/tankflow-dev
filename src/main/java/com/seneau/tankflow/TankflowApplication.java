package com.seneau.tankflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application principal de tankflow - Système de traçabilité des tanks de chlore.
 *
 * Architecture:
 * - config/ : Configuration Spring et sécurité
 * - data/ : Entités JPA, repositories
 * - service/ : Logique métier
 * - web/ : Controllers, DTOs, exceptions
 *
 * Features activés:
 * - @EnableScheduling : Pour les alertes calculées toutes les 15 minutes
 * - Spring Data JPA : Accès aux données
 * - Spring Security : Authentification/autorisation
 */
@SpringBootApplication
@EnableScheduling
public class TankflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TankflowApplication.class, args);
    }
}
