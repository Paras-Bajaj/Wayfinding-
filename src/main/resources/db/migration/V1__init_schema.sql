-- src/main/resources/db/migration/V1__init_schema.sql

CREATE TABLE buildings (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255)
);

CREATE TABLE floors (
    id BIGSERIAL PRIMARY KEY,
    building_id BIGINT NOT NULL REFERENCES buildings(id) ON DELETE CASCADE,
    level INT NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE nodes (
    id BIGSERIAL PRIMARY KEY,
    floor_id BIGINT NOT NULL REFERENCES floors(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    node_type VARCHAR(32) NOT NULL,
    x_coord DOUBLE PRECISION NOT NULL,
    y_coord DOUBLE PRECISION NOT NULL,
    accessible BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_node_floor ON nodes(floor_id);
CREATE INDEX idx_node_type ON nodes(node_type);

CREATE TABLE edges (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    dest_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    distance DOUBLE PRECISION NOT NULL,
    edge_type VARCHAR(32) NOT NULL,
    accessible BOOLEAN NOT NULL DEFAULT TRUE,
    open_from TIME,
    open_to TIME,
    congestion_factor DOUBLE PRECISION NOT NULL DEFAULT 1.0
);
CREATE INDEX idx_edge_source ON edges(source_id);
CREATE INDEX idx_edge_dest ON edges(dest_id);

CREATE TABLE pois (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    poi_type VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL
);
CREATE INDEX idx_poi_type ON pois(poi_type);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(64) NOT NULL
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    action VARCHAR(100),
    detail TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_user ON audit_logs(username);
CREATE INDEX idx_audit_ts ON audit_logs(timestamp);