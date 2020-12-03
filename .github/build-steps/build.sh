#!/bin/bash
set -e
CUSTOM_BUILD_SCRIPT=${CUSTOM_BUILD_SCRIPT:-.github/scripts/build}
if [ -x $CUSTOM_BUILD_SCRIPT ]; then
    echo 'Detected custom build instructions'
    $CUSTOM_BUILD_SCRIPT
elif [ -x 'gradlew' ]; then
    echo 'Detected gradle wrapper, checking for known tasks'
    if ./gradlew tasks | grep '^assemble\s'; then
        echo 'Detected assemble task'
        ./gradlew assemble --parallel
    elif ./gradlew tasks | grep '^build\s'; then
        echo 'Detected build task'
        ./gradlew build --parallel
    else
        echo 'No known tasks, fall back to the default tasks'
        ./gradlew
    fi
elif [ -f 'pom.xml' ]; then
    echo 'Detected Maven pom.xml, running mvn package'
    mvn package -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
else
    echo 'No valid configuration detected, failing'
    echo "To fix, provide an *executable* build script in $CUSTOM_BUILD_SCRIPT"
    exit 1
fi
