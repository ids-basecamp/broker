-- table: edc_federated_cache_catalog
CREATE TABLE IF NOT EXISTS edc_federated_cache_catalog
(
    federated_cache_catalog_id VARCHAR NOT NULL,
    name                       VARCHAR,
    target_url                 VARCHAR,
    supported_protocols        VARCHAR,
    PRIMARY KEY (federated_cache_catalog_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS edc_federated_cache_catalog_id_index
    ON edc_federated_cache_catalog (federated_cache_catalog_id);