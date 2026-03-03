#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# setup.sh
# One-shot local environment setup. Run once after cloning.
# Usage: bash scripts/setup.sh
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

info()    { echo -e "${GREEN}[INFO]${NC}  $*"; }
warning() { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

# ─── Prerequisites check ──────────────────────────────────────────────────────
info "Checking prerequisites..."

command -v docker  &>/dev/null || error "Docker is not installed. https://docs.docker.com/get-docker/"
command -v curl    &>/dev/null || warning "curl not found — seed-data.sh won't work"

DOCKER_COMPOSE_VERSION=$(docker compose version --short 2>/dev/null || echo "")
if [[ -z "$DOCKER_COMPOSE_VERSION" ]]; then
  error "Docker Compose v2 not found. Update Docker Desktop or install the plugin."
fi
info "Docker Compose ${DOCKER_COMPOSE_VERSION} detected."

# ─── Build & start ────────────────────────────────────────────────────────────
info "Building Docker images (this may take a few minutes on first run)..."
docker compose build --no-cache

info "Starting services..."
docker compose up -d

# ─── Wait for services ────────────────────────────────────────────────────────
info "Waiting for services to become healthy..."

wait_for() {
  local name="$1" url="$2" retries=20 delay=5
  for i in $(seq 1 $retries); do
    if curl -sf "$url" &>/dev/null; then
      info "$name is UP."
      return 0
    fi
    echo -n "."
    sleep $delay
  done
  error "$name did not become healthy in time. Check: docker compose logs $name"
}

wait_for "PostgreSQL"  "http://localhost:5432" || true   # pg doesn't serve HTTP
wait_for "ML Service"  "http://localhost:8082/health"
wait_for "API Gateway" "http://localhost:8080/actuator/health"

echo ""
info "All services are running!"
echo ""
echo "  API Gateway : http://localhost:8080/api/health"
echo "  ML Service  : http://localhost:8082/health"
echo "  Actuator    : http://localhost:8080/actuator/health"
echo ""
info "Run 'bash scripts/seed-data.sh' to populate sample transactions."
