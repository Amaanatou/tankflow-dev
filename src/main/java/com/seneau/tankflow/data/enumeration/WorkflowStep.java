package com.seneau.tankflow.data.enumeration;

public enum WorkflowStep {
    SHIPMENT_SENT(1, "Expédition fournisseur"),
    MAIN_RECEPTION(2, "Réception site principal"),
    ROUTED_TO_FACTORY(3, "Acheminement vers usine"),
    FACTORY_RECEPTION(4, "Réception usine"),
    START_USAGE(5, "Mise en utilisation"),
    END_USAGE(6, "Fin utilisation"),
    RETURN_TO_MAIN(7, "Retour vers site principal"),
    MAIN_RETURN_RECEPTION(8, "Réception tanks vides"),
    RETURN_TO_SUPPLIER(9, "Expédition retour fournisseur");

    private final int stepNumber;
    private final String label;

    WorkflowStep(int stepNumber, String label) {
        this.stepNumber = stepNumber;
        this.label = label;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getLabel() {
        return label;
    }

    public static WorkflowStep fromStepNumber(int stepNumber) {
        for (WorkflowStep step : WorkflowStep.values()) {
            if (step.stepNumber == stepNumber) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown step number: " + stepNumber);
    }
}
