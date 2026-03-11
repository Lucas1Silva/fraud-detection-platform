"""
Fraud Detection ML Service
FastAPI application exposing model inference endpoints.
Sprint 1: updated /predict with realistic mock logic driven by amount + time-of-day.
Sprint 2: real XGBoost model + SHAP explainability.
"""

from __future__ import annotations

import logging
import random
import time

from fastapi import FastAPI
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
class PredictRequest(BaseModel):
    """Transaction features sent by the API Gateway for scoring."""
    amount: float = Field(..., gt=0, examples=[1500.00])
    merchant_category: str = Field(..., examples=["electronics"])
    hour_of_day: int = Field(..., ge=0, le=23, examples=[3])
    day_of_week: int = Field(..., ge=1, le=7, examples=[6])
    latitude: float = Field(default=0.0, examples=[-8.05])
    longitude: float = Field(default=0.0, examples=[-34.87])


class PredictResponse(BaseModel):
    model_config = ConfigDict(protected_namespaces=())

    fraud_score: float = Field(..., ge=0.0, le=1.0)
    is_fraud: bool
    model_version: str


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
    logger.info("ML Service starting up — Sprint 1 mock mode with simulated scoring logic")


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
    """Readiness probe — Sprint 1 returns healthy stubs."""
    return ReadinessResponse(
        status="READY",
        model_loaded=False,
        database_connected=False,
    )


@app.post("/predict", response_model=PredictResponse, tags=["Inference"])
async def predict(request: PredictRequest) -> PredictResponse:
    """
    Score a transaction for fraud risk.

    Sprint 1 mock logic:
    - amount > 5000 AND hour_of_day in [0..5]  → fraud_score = 0.85
    - amount > 10000                            → fraud_score = 0.72
    - otherwise                                 → fraud_score = random(0.01, 0.30)

    Sprint 2 will replace this with a trained XGBoost model + SHAP values.
    """
    logger.info(
        "Predict | amount=%.2f category=%s hour=%d day=%d",
        request.amount,
        request.merchant_category,
        request.hour_of_day,
        request.day_of_week,
    )

    # ── Simulated scoring logic ───────────────────────────────────────────────
    is_late_night = 0 <= request.hour_of_day <= 5

    if request.amount > 5000 and is_late_night:
        fraud_score = 0.85
    elif request.amount > 10000:
        fraud_score = 0.72
    else:
        fraud_score = round(random.uniform(0.01, 0.30), 4)

    is_fraud = fraud_score > 0.7

    logger.info("Scored: fraud_score=%.4f is_fraud=%s", fraud_score, is_fraud)

    return PredictResponse(
        fraud_score=round(fraud_score, 4),
        is_fraud=is_fraud,
        model_version="mock-v1.0",
    )
