#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# seed-data.sh
# Seeds the local PostgreSQL with sample transactions for development/testing.
# Usage: bash scripts/seed-data.sh
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

API_URL="${API_URL:-http://localhost:8082}"
PREDICT_URL="${API_URL}/predict"

echo "Seeding fraud detection platform at ${API_URL} ..."

seed_transaction() {
  local txn_id="$1"
  local amount="$2"
  local merchant="$3"
  local card="$4"

  response=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "${PREDICT_URL}" \
    -H "Content-Type: application/json" \
    -d "{
      \"transaction_id\": \"${txn_id}\",
      \"amount\": ${amount},
      \"merchant_id\": \"${merchant}\",
      \"card_last4\": \"${card}\",
      \"timestamp\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\"
    }")

  echo "  [${response}] ${txn_id} | amount=${amount} | merchant=${merchant}"
}

echo ""
echo "─── Low-risk transactions ───────────────────────────────"
seed_transaction "txn-seed-001" "45.00"   "merch-grocery-01" "1234"
seed_transaction "txn-seed-002" "120.50"  "merch-pharmacy-02" "5678"
seed_transaction "txn-seed-003" "299.99"  "merch-electronics-03" "9012"

echo ""
echo "─── Medium-risk transactions ────────────────────────────"
seed_transaction "txn-seed-004" "750.00"  "merch-online-04" "3456"
seed_transaction "txn-seed-005" "1200.00" "merch-jewelry-05" "7890"

echo ""
echo "─── High-risk transactions ──────────────────────────────"
seed_transaction "txn-seed-006" "5500.00" "merch-unknown-06" "1111"
seed_transaction "txn-seed-007" "9999.99" "merch-offshore-07" "9999"

echo ""
echo "Seed complete."
