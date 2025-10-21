-- Flyway migration V1: create initial schema for users and roles

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT pk_usuario_roles PRIMARY KEY (usuario_id, role_id)
);

-- Optional: seed a default role and admin user (comment out if you prefer to create via app)
INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ROLE_ADMIN');

-- NOTE: do not insert admin user with plaintext password in production; hashing required.
-- INSERT INTO usuarios (username, password) VALUES ('admin', 'changeme') ON CONFLICT DO NOTHING;
-- INSERT INTO usuario_roles (usuario_id, role_id) SELECT u.id, r.id FROM usuarios u, roles r WHERE u.username='admin' AND r.name='ROLE_ADMIN';
