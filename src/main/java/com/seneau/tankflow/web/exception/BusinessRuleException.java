package com.seneau.tankflow.web.exception;

/**
 * Exception levée quand une règle métier est violée.
 * Résulte en HTTP 409 Conflict.
 * Exemples: code doublon, transition invalide, etc.
 */
public class BusinessRuleException extends RuntimeException {

    private final String ruleViolated;

    public BusinessRuleException(String message) {
        super(message);
        this.ruleViolated = null;
    }

    public BusinessRuleException(String message, String ruleViolated) {
        super(message);
        this.ruleViolated = ruleViolated;
    }

    public String getRuleViolated() {
        return ruleViolated;
    }
}
