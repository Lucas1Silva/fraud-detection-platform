"""
Fraud Detection ML Service
FastAPI application exposing model inference endpoints.
Sprint 0: mock responses — real model wired in Sprint 2.
"""

from __future__ import annotations

import logging
import time
from typing import Optional
from datetime import datetime

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, ConfigDict, Field

# ─── Logging ──────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
)
logger = logging.getLogger("ml-service")

# ─── App ──────────────────────────────────────────────────────────────────────
app = FastAPI(
    title="Fraud Detection ML Service",
    description="Real-time fraud scoring with XGBoost + SHAP explainability",
    version="0.1.0",
)

_startup_time = time.time()


# ─── Schemas ──────────────────────────────────────────────────────────────────
class TransactionRequest(BaseModel):
    transaction_id: str = Field(..., examples=["txn-001"])
    amount: float = Field(..., gt=0, examples=[1500.00])
    merchant_id: str = Field(..., examples=["merch-42"])
    card_last4: str = Field(..., min_length=4, max_length=4, examples=["9999"])
    timestamp: Optional[datetime] = Field(default=None)


class PredictionResponse(BaseModel):
    model_config = ConfigDict(protected_namespaces=())

    transaction_id: str
    fraud_probability: float = Field(..., ge=0.0, le=1.0)
    is_fraud: bool
    risk_score: int = Field(..., ge=0, le=100, description="0=safe, 100=definite fraud")
    model_version: str
    explanation: dict


class HealthResponse(BaseModel):
    status: str
    uptime_seconds: float
    version: str


class ReadinessResponse(BaseModel):
    model_config = ConfigDict(protected_namespaces=())

    status: str
    model_loaded: bool
    database_connected: bool


# ─── Startup / Shutdown ───────────────────────────────────────────────────────
@app.on_event("startup")
async def on_startup() -> None:
    logger.info("ML Service starting up — Sprint 0 mock mode")


@app.on_event("shutdown")
async def on_shutdown() -> None:
    logger.info("ML Service shutting down")


# ─── Endpoints ────────────────────────────────────────────────────────────────
@app.get("/health", response_model=HealthResponse, tags=["Observability"])
async def health() -> HealthResponse:
    """Liveness probe — always returns 200 while the process is running."""
    return HealthResponse(
        status="UP",
        uptime_seconds=round(time.time() - _startup_time, 2),
        version="0.1.0",
    )


@app.get("/ready", response_model=ReadinessResponse, tags=["Observability"])
async def ready() -> ReadinessResponse:
    """
    Readiness probe — Sprint 0 returns healthy stubs.
    Sprint 2 will check real model artifact + DB connection.
    """
    return ReadinessResponse(
        status="READY",
        model_loaded=False,   # no real model yet
        database_connected=False,  # no DB query yet
    )


@app.post("/predict", response_model=PredictionResponse, tags=["Inference"])
async def predict(request: TransactionRequest) -> PredictionResponse:
    """
    Score a transaction for fraud risk.

    Sprint 0: returns a deterministic mock score based on amount thresholds.
    Sprint 2: will call the trained XGBoost model and compute SHAP values.
    """
    logger.info(
        "Predict request | txn=%s amount=%.2f merchant=%s",
        request.transaction_id,
        request.amount,
        request.merchant_id,
    )

    # ── Mock scoring logic (replace with real model in Sprint 2) ──────────────
    if request.amount > 5000:
        prob = 0.92
    elif request.amount > 1000:
        prob = 0.45
    elif request.amount > 500:
        prob = 0.15
    else:
        prob = 0.03

    risk_score = int(prob * 100)
    is_fraud = prob >= 0.5

    explanation = {
        "top_features": [
            {"feature": "amount", "contribution": round(prob * 0.7, 4)},
            {"feature": "merchant_risk", "contribution": round(prob * 0.2, 4)},
            {"feature": "card_velocity", "contribution": round(prob * 0.1, 4)},
        ],
        "note": "SHAP values available from Sprint 3",
    }

    return PredictionResponse(
        transaction_id=request.transaction_id,
        fraud_probability=round(prob, 4),
        is_fraud=is_fraud,
        risk_score=risk_score,
        model_version="mock-v0.1",
        explanation=explanation,
    )
