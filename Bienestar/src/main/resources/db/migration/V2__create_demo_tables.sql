-- V2: create demo tables for clientes, servicios, citas

CREATE TABLE IF NOT EXISTS clientes (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  email VARCHAR(255),
  telefono VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS servicios (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  descripcion TEXT,
  precio NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS citas (
  id BIGSERIAL PRIMARY KEY,
  cliente_id BIGINT,
  servicio_id BIGINT,
  fecha_hora TIMESTAMP,
  estado VARCHAR(50),
  CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
  CONSTRAINT fk_cita_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE SET NULL
);
