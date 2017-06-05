#!/bin/bash

echo "$1/cflow.jar"

mkdir -p "$1/bin"
javac -d "$1/bin"  -Xlint $(find "$1/cflowCode" -name \*.java) -cp "$1/cflow.jar"
cd "$1/bin/"
java -cp .:../cflow.jar "${@:2}"

