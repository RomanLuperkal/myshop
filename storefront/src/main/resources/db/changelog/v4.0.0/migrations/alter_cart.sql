ALTER TABLE cart
    RENAME COLUMN user_ip TO account_id;

ALTER TABLE cart
    ALTER COLUMN account_id TYPE BIGINT
        USING account_id::BIGINT;