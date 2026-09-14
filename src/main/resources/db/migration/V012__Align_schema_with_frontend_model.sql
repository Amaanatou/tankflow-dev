-- Align backend schema with frontend model
-- Adds missing columns and updates enums

BEGIN;

-- 1. Update expedition table
ALTER TABLE expedition
    ADD COLUMN IF NOT EXISTS transporteur VARCHAR(255),
    ALTER COLUMN type TYPE VARCHAR(50) USING type::VARCHAR,
    ALTER COLUMN statut TYPE VARCHAR(50) USING statut::VARCHAR;

-- 2. Update cycle table
ALTER TABLE cycle
    ADD COLUMN IF NOT EXISTS position VARCHAR(50),
    ADD COLUMN IF NOT EXISTS localisation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS zone VARCHAR(100);

-- 3. Update workflow_event table
-- Drop old columns if they exist (locationId, zoneId)
ALTER TABLE workflow_event
    ADD COLUMN IF NOT EXISTS site VARCHAR(255),
    ADD COLUMN IF NOT EXISTS zone_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS tank_condition VARCHAR(50),
    ADD COLUMN IF NOT EXISTS safety_bell_present BOOLEAN,
    ADD COLUMN IF NOT EXISTS document_reference VARCHAR(255),
    ADD COLUMN IF NOT EXISTS comment TEXT;

-- Rename zone to zone_name if locationId/zoneId were being used
-- Keep old columns for backward compatibility but mark as deprecated

-- Create indexes for performance on new columns
CREATE INDEX IF NOT EXISTS idx_cycle_position ON cycle(position);
CREATE INDEX IF NOT EXISTS idx_workflow_event_site ON workflow_event(site);
CREATE INDEX IF NOT EXISTS idx_workflow_event_tank_condition ON workflow_event(tank_condition);

-- Add default values for existing rows (if needed)
UPDATE cycle SET position = 'HORS_CYCLE' WHERE position IS NULL;
UPDATE workflow_event SET tank_condition = 'INCONNU' WHERE tank_condition IS NULL;

COMMIT;
