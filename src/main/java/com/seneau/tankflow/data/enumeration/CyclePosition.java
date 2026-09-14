package com.seneau.tankflow.data.enumeration;

public enum CyclePosition {
    EN_TRANSIT("En transit"),
    STOCKE("Stocké"),
    EN_UTILISATION("En utilisation"),
    VIDE("Vide"),
    HORS_CYCLE("Hors cycle");

    private final String label;

    CyclePosition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
