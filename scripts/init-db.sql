-- ─────────────────────────────────────────────────────────────────────────────
-- init-db.sql
-- Runs once when the PostgreSQL container first starts.
-- Sprint 0: creates extensions and placeholder schema.
-- Sprint 1: full schema via Flyway migrations.
-- ─────────────────────────────────────────────────────────────────────────────

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ─── Sprint 0 stub tables ────────────────────────────────────────────────────
-- These will be replaced by Flyway migrations in Sprint 1.

CREATE TABLE IF NOT EXISTS transactions (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id   VARCHAR(100) NOT NULL UNIQUE,
    amount           NUMERIC(15, 2) NOT NULL,
    merchant_id      VARCHAR(100) NOT NULL,
    card_last4       VARCHAR(4) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS fraud_scores (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id    UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    fraud_probability NUMERIC(6, 4) NOT NULL,
    risk_score        SMALLINT NOT NULL CHECK (risk_score BETWEEN 0 AND 100),
    model_version     VARCHAR(50) NOT NULL,
    explanation       JSONB,
    scored_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_created_at   ON transactions(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_status       ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_fraud_scores_transaction  ON fraud_scores(transaction_id);
