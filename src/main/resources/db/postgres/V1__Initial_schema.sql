-- PostgreSQL-compatible version of the initial schema (mirrors db/migration/V1 for MySQL).

CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN
);

-- On utilise toujours une liste de roles, pour permettre l'ajout de nouveaux roles a un utilisateur
CREATE TABLE IF NOT EXISTS users_roles (
    users_id INT REFERENCES users(id),
    roles_id INT REFERENCES roles(id),
    PRIMARY KEY (users_id, roles_id)
);

-- Le nom des roles doit commencer par ROLE_ en Spring Security
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_USER'),
('ROLE_TESTER');
