#!/bin/bash
cd /home/kavia/workspace/code-generation/personal-notes-manager-98765-158274/android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

