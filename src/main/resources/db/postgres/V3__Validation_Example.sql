-- Table for the ValidationExample entity (PostgreSQL).

CREATE TABLE IF NOT EXISTS validation_example (
    id BIGINT NOT NULL,
    name VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    age INT,
    phone_number VARCHAR(255),
    agreed_to_terms BOOLEAN,
    birthdate DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS validation_example_roles (
    validation_example_id BIGINT NOT NULL,
    role VARCHAR(255),
    FOREIGN KEY (validation_example_id) REFERENCES validation_example(id)
);
