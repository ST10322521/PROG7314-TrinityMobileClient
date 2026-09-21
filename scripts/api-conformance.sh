#!/usr/bin/env bash
# J4 (James, stand-in): API endpoint conformance check.
#
# Runs the mock server's own smoke test (which exercises every spec.md
# endpoint group: agents, tasks/commands, listeners tcp/http, payloads,
# downloads, config, auth) against a running backend and records the
# result as evidence in docs/API_CONFORMANCE.md.
#
# Usage:
#   scripts/api-conformance.sh [BASE_URL]      # default http://127.0.0.1:8000
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SANDBOX_ROOT="$(cd "$ROOT/../.." && pwd)"
BASE_URL="${1:-http://127.0.0.1:8000}"
MOCK_DIR="$SANDBOX_ROOT/Trinity-Mock-API"
PY="$MOCK_DIR/.venv/bin/python"
[ -x "$PY" ] || PY=python3

cd "$MOCK_DIR"
echo "== api-conformance: running smoke_test.py against $BASE_URL =="
# smoke_test.py hardcodes its base URL; patch a temp copy so this script
# can target any backend (e.g. a forwarded port or a test host).
patched="$(mktemp /tmp/trinity_smoke_XXXXXX.py)"
sed "s|^BASE = .*|BASE = \"$BASE_URL\"|" smoke_test.py > "$patched"
if "$PY" "$patched"; then
  STATUS="PASS"
else
  STATUS="FAIL"
fi
rm -f "$patched"

DOC="$ROOT/docs/API_CONFORMANCE.md"
ts="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
{
  echo ""
  echo "## Run: $ts (base $BASE_URL) — $STATUS"
  echo ""
} >> "$DOC"
echo "Recorded in $DOC — status: $STATUS"
