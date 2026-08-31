CREATE TABLE workflow_event (
    id BIGSERIAL PRIMARY KEY,
    cycle_id BIGINT NOT NULL,
    step_number INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    location_id BIGINT,
    zone_id BIGINT,
    performed_by_user_id BIGINT,
    event_timestamp TIMESTAMP NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (cycle_id) REFERENCES tank_cycle(id)
);

CREATE INDEX idx_workflow_event_cycle ON workflow_event(cycle_id);
CREATE INDEX idx_workflow_event_type ON workflow_event(event_type);
CREATE INDEX idx_workflow_event_idempotency ON workflow_event(idempotency_key);
CREATE INDEX idx_workflow_event_timestamp ON workflow_event(event_timestamp);
