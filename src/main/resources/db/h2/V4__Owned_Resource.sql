-- Table for the OwnedResource entity (H2) - used by the @PostAuthorize example.

CREATE TABLE IF NOT EXISTS owned_resource (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner VARCHAR(255) NOT NULL,
    content VARCHAR(255),
    PRIMARY KEY (id)
);
