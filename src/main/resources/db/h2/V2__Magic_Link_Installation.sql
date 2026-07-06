-- H2-compatible version of the magic-link columns (mirrors db/migration/V2 for MySQL).
-- H2 does not accept several ADD COLUMN clauses in a single statement, so we split them.

ALTER TABLE users ADD COLUMN claim_token VARCHAR(255) UNIQUE;
ALTER TABLE users ADD COLUMN user_creator INT;
ALTER TABLE users ADD COLUMN token_expiration TIMESTAMP;
