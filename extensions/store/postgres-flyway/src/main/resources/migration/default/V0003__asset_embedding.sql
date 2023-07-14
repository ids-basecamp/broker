ALTER TABLE edc_contract_offer
    ADD COLUMN asset JSON NOT NULL,
    DROP COLUMN asset_id;