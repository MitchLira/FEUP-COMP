#!/bin/bash


mkdir -p bin
javac -d bin  -Xlint $(find . -name \*.java) -cp laraOutput/cflow.jar
cd bin/
java -cp .:../laraOutput/cflow.jar "$@"

