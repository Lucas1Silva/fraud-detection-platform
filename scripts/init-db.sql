-- ─────────────────────────────────────────────────────────────────────────────
-- init-db.sql
-- Runs once when the PostgreSQL container first starts (fresh volume only).
-- Sprint 1: full schema aligned with the JPA Transaction entity.
-- Hibernate ddl-auto=update handles subsequent schema evolution.
-- ─────────────────────────────────────────────────────────────────────────────

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ─── Sprint 1 schema ─────────────────────────────────────────────────────────
-- Drop Sprint 0 stub tables if present (fresh installs only — volume is new).
DROP TABLE IF EXISTS fraud_scores;
DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount                  NUMERIC(15, 2)  NOT NULL,
    merchant_name           VARCHAR(255)    NOT NULL,
    merchant_category       VARCHAR(100)    NOT NULL,
    card_number_hash        VARCHAR(255)    NOT NULL,
    transaction_timestamp   TIMESTAMP       NOT NULL DEFAULT NOW(),
    latitude                DOUBLE PRECISION,
    longitude               DOUBLE PRECISION,
    is_fraud                BOOLEAN         NOT NULL DEFAULT FALSE,
    fraud_score             DOUBLE PRECISION,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Performance indexes
CREATE INDEX idx_transactions_created_at       ON transactions(created_at DESC);
CREATE INDEX idx_transactions_is_fraud         ON transactions(is_fraud);
CREATE INDEX idx_transactions_fraud_score      ON transactions(fraud_score);
CREATE INDEX idx_transactions_timestamp        ON transactions(transaction_timestamp DESC);
CREATE INDEX idx_transactions_merchant_cat     ON transactions(merchant_category);
