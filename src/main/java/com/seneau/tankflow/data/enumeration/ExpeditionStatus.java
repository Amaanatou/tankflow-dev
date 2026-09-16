package com.seneau.tankflow.data.enumeration;

public enum ExpeditionStatus {
    BROUILLON("Brouillon"),
    VALIDEE("Validée"),
    EN_COURS("En cours"),
    TERMINEE("Terminée");

    private final String label;

    ExpeditionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
