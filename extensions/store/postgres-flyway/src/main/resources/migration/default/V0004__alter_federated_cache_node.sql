ALTER TABLE edc_federated_cache_node
    ADD COLUMN online_status BOOLEAN NOT NULL,
    ADD COLUMN last_crawled BIGINT,
    ADD COLUMN contract_offers_count INTEGER NOT NULL;