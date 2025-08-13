#!/usr/bin/env bash
set -euo pipefail
# Proxy script to run the Android frontend's Gradle wrapper from the workspace root.
cd "$(dirname "$0")/android_frontend"
exec bash "./gradlew" "$@"
