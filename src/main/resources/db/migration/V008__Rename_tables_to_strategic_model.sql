-- Align schema to 5-table strategic model
-- Add FK constraint for workflow_event to cycle
-- Note: Tables are already renamed tank_asset→tank and tank_cycle→cycle by V006
-- Indexes are already renamed by V006

BEGIN;

-- Mettre à jour la FK dans workflow_event (vers cycle)
-- V006 a déjà renommé les tables et les index, on met juste à jour cette contrainte FK
ALTER TABLE workflow_event DROP CONSTRAINT IF EXISTS workflow_event_cycle_id_fkey;
ALTER TABLE workflow_event ADD CONSTRAINT workflow_event_cycle_id_fkey FOREIGN KEY (cycle_id) REFERENCES cycle(id);

COMMIT;
