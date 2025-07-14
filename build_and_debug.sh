#!/bin/bash

# Script to build and deploy the FLFM plugin for debugging
# Usage: ./build_and_debug.sh

set -e

PROJECT_DIR="/home/ryan/repos/mm-plugin/flfm"
IMAGEJ_PLUGINS_DIR="/home/ryan/repos/mm-plugin/mm_install/ij/plugins"
DEBUG_PORT=5005

echo "Building FLFM plugin..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests

echo "Copying plugin to ImageJ plugins directory..."
mkdir -p "$IMAGEJ_PLUGINS_DIR"
cp target/flfm_plugin.jar "$IMAGEJ_PLUGINS_DIR/"

echo "Plugin copied to: $IMAGEJ_PLUGINS_DIR/flfm_plugin.jar"
echo ""
echo "To debug:"
echo "1. Run: ./debug_imagej.sh"
echo "2. In VS Code, use 'Debug Remote JAR' configuration"
echo "3. Load your plugin in ImageJ and set breakpoints"
