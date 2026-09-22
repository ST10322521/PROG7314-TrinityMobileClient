#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
DOTNET_PROJECT_DIR="${1:-$SCRIPT_DIR/../../Trinity/TeamServer}"
SWAGGER_URL="http://localhost:5069/swagger/v1/swagger.json"
TEMP_GEN_DIR="$SCRIPT_DIR/.openapi-tmp"

echo "Starting TeamServer..."
if [ ! -e "$DOTNET_PROJECT_DIR" ]; then
    (cd "$REPO_ROOT"/.. && git clone https://github.com/iamgred/Trinity -b test && grep 'TryGetMethodInfo' 'Trinity/TeamServer/Program.cs' || (cd Trinity && git merge origin/feat/teamserver-swagger-ops))
fi
(cd "$DOTNET_PROJECT_DIR" && (git pull || echo "[gen-openapi] warn: git pull failed — continuing with local TeamServer code" && git log | head) && dotnet run) &
SERVER_PID=$!

cleanup() {
    echo "Stopping TeamServer (PID: $SERVER_PID)..."
    kill "$SERVER_PID" 2>/dev/null || true
}
trap cleanup EXIT

echo "Waiting for API to respond at $SWAGGER_URL..."
MAX_RETRIES=30
RETRY_COUNT=0
until curl -s -f -o /dev/null "$SWAGGER_URL"; do
    sleep 0.5
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]; then
        echo "Error: Timed out waiting for TeamServer to start." >&2
        exit 1
    fi
done
echo "TeamServer is up."

rm -rf "$TEMP_GEN_DIR"
cd "$SCRIPT_DIR"

# AI Disclosure: Gemini gave me the npx command in https://share.gemini.google/iraDqdFYRW2c
npx @openapitools/openapi-generator-cli generate \
    -i "$SWAGGER_URL" \
    -g kotlin \
    -o "$TEMP_GEN_DIR" \
    --additional-properties=library=jvm-retrofit2,serializationLibrary=gson,useCoroutines=true,packageName=Emeris.PROG7314.trinitymobileclient.api,modelPackage=Emeris.PROG7314.trinitymobileclient.api.model,apiPackage=Emeris.PROG7314.trinitymobileclient.api.retrofit \
    --global-property=models,apis,supportingFiles

TARGET_DIR="$REPO_ROOT/app/src/main/java"
mkdir -p "$TARGET_DIR"

cp -r "$TEMP_GEN_DIR/src/main/kotlin/"* "$TARGET_DIR/"
rm -rf "$TEMP_GEN_DIR"

echo "OpenAPI generation and sync complete."
