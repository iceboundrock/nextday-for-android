#!/bin/bash

./gradlew asD
adb devices | tail -n +2 | cut -sf 1 | xargs -I {} adb -s {} install -r app/build/outputs/apk/app-debug.apk
