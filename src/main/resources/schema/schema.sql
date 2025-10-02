CREATE DATABASE eventura_catalog;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE eventura_catalog TO postgres;

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- Movies Table
-- =========================
CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    duration_minutes INT,
    genres TEXT,
    description TEXT,
    poster_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =========================
-- Theaters Table
-- =========================
CREATE TABLE theaters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    address TEXT
);

-- =========================
-- Halls Table
-- =========================
CREATE TABLE halls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    theater_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    seat_layout JSONB NOT NULL,
    capacity INT,
    CONSTRAINT fk_theater FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE
);

-- =========================
-- Indexes
-- =========================
CREATE INDEX idx_movies_active ON movies(active);
CREATE INDEX idx_theaters_city ON theaters(city);
CREATE INDEX idx_halls_theater ON halls(theater_id);
