package com.seneau.tankflow.data.enumeration;

public enum AlertLevel {
    WARNING("Attention", 15),      // J-15
    URGENT("Urgence", 10),         // J-10
    CRITICAL("Critique", 5),       // J-5
    OVERDUE("Dépassement", 0);     // J+0

    private final String label;
    private final int daysThreshold;

    AlertLevel(String label, int daysThreshold) {
        this.label = label;
        this.daysThreshold = daysThreshold;
    }

    public String getLabel() {
        return label;
    }

    public int getDaysThreshold() {
        return daysThreshold;
    }
}
