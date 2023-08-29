ALTER TABLE edc_federated_cache_node
    ADD COLUMN online_status BOOLEAN,
    ADD COLUMN last_crawled BIGINT,
    ADD COLUMN contract_offers_count INTEGER;