#!/bin/bash

# Script to start ImageJ with remote debugging enabled
# Usage: ./debug_imagej.sh

IMAGEJ_DIR="/home/$USER/repos/mm-plugin/mm_install/ij"
DEBUG_PORT=8000

echo "Starting ImageJ with remote debugging on port $DEBUG_PORT"
echo "Connect your debugger to localhost:$DEBUG_PORT"

cd "$IMAGEJ_DIR"
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:$DEBUG_PORT \
     -jar ij.jar

echo "ImageJ with debugging started. You can now connect your debugger."
