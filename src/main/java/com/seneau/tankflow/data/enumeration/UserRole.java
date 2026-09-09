package com.seneau.tankflow.data.enumeration;

public enum UserRole {
    ADMIN("Administrateur Système"),
    COORDINATOR("Coordinateur Logistique"),
    MAGASINIER_CENTRAL("Magasinier Central"),
    MAGASINIER_USINE("Magasinier Usine"),
    AGENT_PRODUCTION("Agent de Production"),
    RESPONSABLE_ACHATS("Responsable Achats");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
