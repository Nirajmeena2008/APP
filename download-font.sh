#!/bin/bash
# Download Inter variable font for the project
mkdir -p app/src/main/res/font
cd app/src/main/res/font
wget "https://github.com/google/fonts/raw/main/ofl/inter/Inter%5Bopsz,wght%5D.ttf" \
  -O "Inter-VariableFont_opsz,wght.ttf"
echo "Inter font downloaded to app/src/main/res/font/"
