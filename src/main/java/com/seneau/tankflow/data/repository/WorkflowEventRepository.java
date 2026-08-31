package com.seneau.tankflow.data.repository;

import com.seneau.tankflow.data.model.WorkflowEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowEventRepository extends JpaRepository<WorkflowEvent, Long> {

    List<WorkflowEvent> findByCycleIdOrderByCreatedAtAsc(Long cycleId);

    Optional<WorkflowEvent> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
