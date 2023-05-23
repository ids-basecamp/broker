-- table: edc_federated_cache_node
CREATE TABLE IF NOT EXISTS edc_federated_cache_node
(
    name                VARCHAR NOT NULL,
    target_url          VARCHAR NOT NULL,
    supported_protocols JSON NOT NULL,
    PRIMARY KEY (name)
);

COMMENT ON COLUMN edc_federated_cache_node.supported_protocols IS 'Java Supported Protocols List serialized as JSON';
