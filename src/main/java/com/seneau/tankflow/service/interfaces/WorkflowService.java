package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.WorkflowStep;
import com.seneau.tankflow.data.model.WorkflowEvent;

import java.util.List;

public interface WorkflowService {

    WorkflowEvent recordEvent(Long cycleId, WorkflowStep step, Long locationId, Long zoneId, Long performedByUserId, String idempotencyKey);

    List<WorkflowEvent> getEventsByCycle(Long cycleId);

    boolean canTransitionToStep(Long cycleId, Integer nextStepNumber);

    WorkflowStep getCurrentStep(Long cycleId);

    WorkflowStep getNextStep(Long cycleId);

    boolean isIdempotencyKeyProcessed(String idempotencyKey);

    long getTotalEventCount();
}
