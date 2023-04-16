-- table: edc_contract_offer
CREATE TABLE IF NOT EXISTS edc_contract_offer
(
    contract_offer_id VARCHAR NOT NULL,
    policy_id         VARCHAR NOT NULL,
    asset_id          VARCHAR NOT NULL,
    uri_provider      VARCHAR,
    uri_consumer      VARCHAR,
    offer_start       TIMESTAMP WITHOUT TIME ZONE,
    offer_end         TIMESTAMP WITHOUT TIME ZONE,
    contract_start    TIMESTAMP WITHOUT TIME ZONE,
    contract_end      TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY (contract_offer_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS edc_contract_offer_id_index
    ON edc_contract_offer (contract_offer_id);