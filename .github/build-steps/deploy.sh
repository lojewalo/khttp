#!/bin/bash
set -e
CUSTOM_BUILD_SCRIPT=${CUSTOM_BUILD_SCRIPT:-.github/scripts/deploy}
if [ -x $CUSTOM_BUILD_SCRIPT ]; then
    echo 'Detected custom deploy instructions'
    $CUSTOM_BUILD_SCRIPT
elif [ -x 'gradlew' ]; then
    echo 'Detected gradle wrapper, checking for known tasks'
    if ./gradlew tasks | grep '^deploy\s'; then
        echo 'Detected deploy task'
        ./gradlew deploy --parallel
    elif ./gradlew tasks | grep '^publish\s'; then
        echo 'Detected publish task'
        ./gradlew publish --parallel
    else
        echo 'No deploy task'
    fi
elif [ -f 'pom.xml' ]; then
    echo 'Detected Maven pom.xml, running mvn deploy'
    mvn deploy -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
else
    echo 'No valid configuration detected, failing'
    echo "To fix, provide an *executable* build script in $CUSTOM_BUILD_SCRIPT"
    exit 1
fi
