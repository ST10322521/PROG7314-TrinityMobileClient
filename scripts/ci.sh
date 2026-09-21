#!/usr/bin/env bash
# Team Lead: local CI — the same checks GitHub Actions runs.
#
# Usage:
#   scripts/ci.sh                 # build + unit tests
#   BACKEND=http://host:port scripts/ci.sh   # also run live API conformance
set -euo pipefail
cd "$(dirname "$0")/.."

echo "== ci: unit tests =="
./gradlew :app:testDebugUnitTest --no-daemon

echo "== ci: assemble debug =="
./gradlew :app:assembleDebug --no-daemon

if [ -n "${BACKEND:-}" ]; then
  echo "== ci: live API conformance against $BACKEND =="
  scripts/api-conformance.sh "$BACKEND"
fi

echo "== ci: done =="
