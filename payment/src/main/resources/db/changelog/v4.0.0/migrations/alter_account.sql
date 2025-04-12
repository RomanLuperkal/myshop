ALTER TABLE account
    RENAME COLUMN user_ip TO username;

ALTER TABLE account
    ADD COLUMN password VARCHAR;