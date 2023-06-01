-- table: edc_federated_cache_node
CREATE TABLE IF NOT EXISTS edc_federated_cache_node
(
    target_url          VARCHAR NOT NULL,
    name                VARCHAR NOT NULL,
    supported_protocols JSON NOT NULL,
    PRIMARY KEY (target_url)
);

COMMENT ON COLUMN edc_federated_cache_node.supported_protocols IS 'Java Supported Protocols List serialized as JSON';
