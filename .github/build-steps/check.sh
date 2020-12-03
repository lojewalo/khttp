#!/bin/bash
set -e
CUSTOM_BUILD_SCRIPT=${CUSTOM_BUILD_SCRIPT:-.github/scripts/check}
if [ -x $CUSTOM_BUILD_SCRIPT ]; then
    echo 'Detected custom check instructions'
    $CUSTOM_BUILD_SCRIPT
elif [ -x 'gradlew' ]; then
    echo 'Detected gradle wrapper, checking for known tasks'
    if ./gradlew tasks | grep '^check\s'; then
        echo 'Detected check task'
        ./gradlew check --parallel
        if ./gradlew tasks | grep '^jacocoTestReport\s'; then
            ./gradlew jacocoTestReport --parallel
        fi
    else
        echo 'No known check tasks'
    fi
else
    echo 'No valid configuration detected, skipping checks'
    echo "To fix, provide an *executable* build script in $CUSTOM_BUILD_SCRIPT"
fi
