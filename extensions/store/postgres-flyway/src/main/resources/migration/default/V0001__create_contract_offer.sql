-- table: edc_contract_offer
CREATE TABLE IF NOT EXISTS edc_contract_offer
(
    contract_offer_id VARCHAR NOT NULL,
    policy            JSON NOT NULL,
    asset_id          VARCHAR NOT NULL,
    uri_provider      VARCHAR,
    uri_consumer      VARCHAR,
    offer_start       BIGINT,
    offer_end         BIGINT,
    contract_start    BIGINT NOT NULL,
    contract_end      BIGINT NOT NULL,
    PRIMARY KEY (contract_offer_id)
);

COMMENT ON COLUMN edc_contract_offer.policy IS 'Java Policy serialized as JSON';
