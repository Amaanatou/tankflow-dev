package com.seneau.tankflow.data.enumeration;

public enum UserRole {
    ADMIN("Administrateur Système"),
    COORDINATOR("Coordinateur Logistique"),
    WAREHOUSE_CENTRAL("Magasinier Central"),
    WAREHOUSE_FACTORY("Magasinier Usine"),
    PRODUCTION_AGENT("Agent de Production"),
    PROCUREMENT("Responsable Achats");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
