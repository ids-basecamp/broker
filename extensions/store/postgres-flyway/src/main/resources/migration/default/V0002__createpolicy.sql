-- table: edc_policy
CREATE TABLE IF NOT EXISTS edc_policy
(
    policy_id             VARCHAR NOT NULL,
    permissions           JSON,
    prohibitions          JSON,
    duties                JSON,
    extensible_properties JSON,
    inherits_from         VARCHAR,
    assigner              VARCHAR,
    assignee              VARCHAR,
    target                VARCHAR,
    policy_type           VARCHAR NOT NULL,
    created_at            BIGINT NOT NULL,
    PRIMARY KEY (policy_id)
);

COMMENT ON COLUMN edc_policy.permissions IS 'Java List<Permission> serialized as JSON';
COMMENT ON COLUMN edc_policy.prohibitions IS 'Java List<Prohibition> serialized as JSON';
COMMENT ON COLUMN edc_policy.duties IS 'Java List<Duty> serialized as JSON';
COMMENT ON COLUMN edc_policy.extensible_properties IS 'Java Map<String, Object> serialized as JSON';
COMMENT ON COLUMN edc_policy.policy_type IS 'Java PolicyType serialized as JSON';

CREATE UNIQUE INDEX IF NOT EXISTS edc_policy_id_index
    ON edc_policy (policy_id);
