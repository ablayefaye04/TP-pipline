#!/bin/sh
# Deploiement SIMULE : copie le JAR dans un dossier "deploy" (a remplacer par scp, docker push, kubectl...)
set -e
JAR=$(ls target/*.jar | head -n 1)
mkdir -p deploy
cp "$JAR" deploy/app.jar
echo "=== Deploiement simule ==="
echo "JAR deploye : $JAR -> deploy/app.jar"
ls -lh deploy/app.jar
