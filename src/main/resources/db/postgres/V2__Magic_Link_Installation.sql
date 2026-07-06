-- PostgreSQL-compatible version of the magic-link columns (mirrors db/migration/V2 for MySQL).

ALTER TABLE users ADD COLUMN claim_token VARCHAR(255) UNIQUE;
ALTER TABLE users ADD COLUMN user_creator INT;
ALTER TABLE users ADD COLUMN token_expiration TIMESTAMP;
