#!/bin/sh

set -e

cd "$(dirname "$(realpath "$0")")"

echo 'cleaning...'
rm -rf ./target

echo 'compiling...'
javac -d ./target/classes $(find ./src -name "*.java")

echo 'packaging...'
jar cf ./target/qr_scan.jar -C ./target/classes .

ls -l ./target/qr_scan.jar
