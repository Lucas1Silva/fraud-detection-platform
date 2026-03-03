# Fraud Detection Platform

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Python](https://img.shields.io/badge/Python-3.11-blue?logo=python)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![FastAPI](https://img.shields.io/badge/FastAPI-0.110-teal?logo=fastapi)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)

> Real-time fraud detection platform built with a microservices architecture,
> combining Spring Boot for business logic orchestration and FastAPI for ML inference.
> Designed to run entirely on-premise, CPU-only.

---

## Architecture

```
                          ┌─────────────────────────────────────────────┐
                          │              Docker Network (fraud-net)       │
                          │                                               │
   Client / Postman       │   ┌─────────────────┐    ┌────────────────┐  │
        │                 │   │   API Gateway   │    │   ML Service   │  │
        │  HTTP :8080     │   │  (Spring Boot)  │───▶│   (FastAPI)    │  │
        └────────────────▶│   │                 │    │                │  │
                          │   │  /api/health    │    │  /health       │  │
                          │   │  /api/v1/txn    │    │  /ready        │  │
                          │   │  /actuator/**   │    │  /predict      │  │
                          │   └────────┬────────┘    └───────┬────────┘  │
                          │            │                      │           │
                          │            └──────────┬───────────┘           │
                          │                       │                       │
                          │              ┌────────▼────────┐              │
                          │              │   PostgreSQL 16  │              │
                          │              │   fraud_db :5432 │              │
                          │              └─────────────────┘              │
                          └─────────────────────────────────────────────┘
```

## Tech Stack

| Layer          | Technology                          | Purpose                              |
|----------------|-------------------------------------|--------------------------------------|
| API Gateway    | Spring Boot 3.2, Java 17            | REST orchestration, persistence      |
| ML Service     | FastAPI, Python 3.11                | Model inference, SHAP explainability |
| ML Models      | scikit-learn, XGBoost               | Fraud classification                 |
| Database       | PostgreSQL 16                       | Transaction & audit storage          |
| Observability  | Spring Actuator, uvicorn logs       | Health checks & metrics              |
| Infra          | Docker Compose                      | Local orchestration                  |

---

## Quick Start

### Prerequisites

- Docker Desktop >= 24 (or Docker Engine + Compose v2)
- 16 GB RAM recommended
- Ports 8080, 8082, 5432 available

### 1. Clone & Run

```bash
git clone https://github.com/your-username/fraud-detection-platform.git
cd fraud-detection-platform

# Build images and start all services
docker compose up --build

# Run in background
docker compose up --build -d
```

### 2. Verify Services

```bash
# API Gateway health
curl http://localhost:8080/api/health

# Spring Actuator
curl http://localhost:8080/actuator/health

# ML Service health
curl http://localhost:8082/health

# ML Service readiness
curl http://localhost:8082/ready
```

### 3. Test Fraud Prediction (mock)

```bash
curl -X POST http://localhost:8082/predict \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "txn-001",
    "amount": 1500.00,
    "merchant_id": "merch-42",
    "card_last4": "9999",
    "timestamp": "2026-03-01T10:00:00Z"
  }'
```

### 4. Stop Services

```bash
docker compose down          # stop containers
docker compose down -v       # stop + remove volumes (wipes DB)
```

---

## Project Structure

```
fraud-detection-platform/
├── api-gateway/                    # Spring Boot microservice
│   ├── src/main/java/com/frauddetection/
│   │   ├── controller/             # REST controllers
│   │   ├── model/                  # JPA entities + DTOs
│   │   ├── service/                # Business logic
│   │   └── config/                 # Spring configuration
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── ml-service/                     # FastAPI ML microservice
│   ├── main.py
│   ├── requirements.txt
│   └── models/                     # Trained model artifacts (gitignored)
├── docker/
│   ├── Dockerfile.api-gateway
│   └── Dockerfile.ml-service
├── docs/
│   └── architecture.md
├── scripts/
│   ├── init-db.sql
│   └── seed-data.sh
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Sprint Roadmap

| Sprint | Goal                                    | Status      |
|--------|-----------------------------------------|-------------|
| **0**  | Walking Skeleton – infra + stubs        | ✅ Complete  |
| **1**  | Transaction ingestion API + DB schema   | 🔲 Planned   |
| **2**  | ML model training pipeline (XGBoost)    | 🔲 Planned   |
| **3**  | Real-time scoring + SHAP explainability | 🔲 Planned   |
| **4**  | Alerting, audit log, dashboard          | 🔲 Planned   |
| **5**  | Performance tuning + load testing       | 🔲 Planned   |

---

## Environment Variables

| Variable                  | Service     | Default                                         |
|---------------------------|-------------|-------------------------------------------------|
| `POSTGRES_DB`             | postgres    | `fraud_db`                                      |
| `POSTGRES_USER`           | postgres    | `fraud_user`                                    |
| `POSTGRES_PASSWORD`       | postgres    | `fraud_pass`                                    |
| `SPRING_PROFILES_ACTIVE`  | api-gateway | `docker`                                        |
| `ML_SERVICE_URL`          | api-gateway | `http://ml-service:8082`                        |
| `DATABASE_URL`            | ml-service  | `postgresql://fraud_user:fraud_pass@postgres/fraud_db` |


