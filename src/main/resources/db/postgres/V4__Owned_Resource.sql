-- Table for the OwnedResource entity (PostgreSQL) - used by the @PostAuthorize example.

CREATE TABLE IF NOT EXISTS owned_resource (
    id BIGSERIAL PRIMARY KEY,
    owner VARCHAR(255) NOT NULL,
    content VARCHAR(255)
);
