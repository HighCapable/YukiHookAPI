#!/bin/sh
set -eu

cd ..
./gradlew :yukihook-core:publishKDoc :yukihook-extension-kavaref:publishKDoc :yukihook-runtime-libxposed:publishKDoc :yukihook-runtime-xposed82:publishKDoc