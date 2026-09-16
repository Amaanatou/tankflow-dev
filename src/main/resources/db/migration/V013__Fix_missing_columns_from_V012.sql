-- Fix missing columns from V012 (transaction conflict issue)
-- Re-apply all missing columns with IF NOT EXISTS for safety

-- 1. Ensure workflow_event has zone column (main blocker)
ALTER TABLE workflow_event
    ADD COLUMN IF NOT EXISTS zone VARCHAR(100);

-- 2. Ensure all other expected columns exist
ALTER TABLE workflow_event
    ADD COLUMN IF NOT EXISTS site VARCHAR(255),
    ADD COLUMN IF NOT EXISTS tank_condition VARCHAR(50),
    ADD COLUMN IF NOT EXISTS safety_bell_present BOOLEAN,
    ADD COLUMN IF NOT EXISTS document_reference VARCHAR(255),
    ADD COLUMN IF NOT EXISTS comment TEXT;

-- 3. Ensure cycle columns exist
ALTER TABLE cycle
    ADD COLUMN IF NOT EXISTS position VARCHAR(50),
    ADD COLUMN IF NOT EXISTS localisation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS zone VARCHAR(100);

-- 4. Ensure expedition columns exist
ALTER TABLE expedition
    ADD COLUMN IF NOT EXISTS transporteur VARCHAR(255);

-- 5. Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_cycle_position ON cycle(position);
CREATE INDEX IF NOT EXISTS idx_workflow_event_site ON workflow_event(site);
CREATE INDEX IF NOT EXISTS idx_workflow_event_tank_condition ON workflow_event(tank_condition);

-- 6. Set defaults for existing rows (idempotent)
UPDATE cycle SET position = 'HORS_CYCLE' WHERE position IS NULL;
UPDATE workflow_event SET tank_condition = 'INCONNU' WHERE tank_condition IS NULL;
