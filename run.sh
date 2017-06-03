#!/bin/bash


DIR="$(dirname $0)"
echo "$DIR"
mkdir -p "$DIR/bin"
javac -d "$DIR/bin"  -Xlint $(find $DIR -name \*.java) -cp "$DIR/laraOutput/cflow.jar"
cd "$DIR/bin/"
java -cp .:../laraOutput/cflow.jar "$@"

