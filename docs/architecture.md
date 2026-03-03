# Architecture Overview

## Sprint 0 — Walking Skeleton

### Component Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Docker Compose Stack                          │
│                                                                      │
│  ┌──────────────────────────┐      ┌─────────────────────────────┐  │
│  │       API Gateway         │      │        ML Service           │  │
│  │    (Spring Boot 3.2)      │─────▶│       (FastAPI 0.110)       │  │
│  │                           │      │                             │  │
│  │  GET  /api/health         │      │  GET  /health               │  │
│  │  GET  /actuator/health    │      │  GET  /ready                │  │
│  │  POST /api/v1/transactions│      │  POST /predict              │  │
│  │       (Sprint 1)          │      │       (mock → real S2)      │  │
│  │                           │      │                             │  │
│  │  Port: 8080               │      │  Port: 8082                 │  │
│  └────────────┬──────────────┘      └──────────────┬──────────────┘  │
│               │                                    │                 │
│               └──────────────┬─────────────────────┘                 │
│                              │                                       │
│                   ┌──────────▼──────────┐                           │
│                   │    PostgreSQL 16     │                           │
│                   │    fraud_db :5432    │                           │
│                   │                     │                           │
│                   │  tables (Sprint 1):  │                           │
│                   │  - transactions      │                           │
│                   │  - fraud_scores      │                           │
│                   │  - audit_log         │                           │
│                   └─────────────────────┘                           │
└──────────────────────────────────────────────────────────────────────┘
```

### Data Flow (Sprint 3+)

```
Client
  │
  │ POST /api/v1/transactions
  ▼
API Gateway
  │ 1. Validate & persist transaction (PENDING)
  │ 2. Extract features
  │ 3. POST /predict → ML Service
  ▼
ML Service
  │ 4. XGBoost inference
  │ 5. SHAP explanation
  │ return { fraud_probability, risk_score, explanation }
  ▼
API Gateway
  │ 6. Update transaction status (APPROVED | FLAGGED)
  │ 7. Write audit log
  │ 8. Return response to client
  ▼
Client
```

### Technology Decisions

| Decision              | Choice           | Rationale                                              |
|-----------------------|------------------|--------------------------------------------------------|
| API framework         | Spring Boot 3.2  | Industry standard, JPA, production-grade actuator      |
| ML framework          | FastAPI          | Async, fast, native Pydantic validation                |
| Model                 | XGBoost          | SOTA for tabular fraud data, CPU-efficient             |
| Explainability        | SHAP             | Model-agnostic, TreeExplainer for XGBoost              |
| Database              | PostgreSQL 16    | ACID, JSONB for flexible feature storage               |
| Orchestration         | Docker Compose   | Zero-cloud, local-first, simple dependency ordering    |
| Inter-service comms   | REST/HTTP        | Simple, debuggable; replace with Kafka in Sprint 5     |

### Constraints

- CPU-only inference (no GPU)
- 16 GB RAM total budget
- Zero cloud dependencies
- All data stays on-premise
