#!/bin/sh

set -e

cd "$(dirname "$(realpath "$0")")"

QR_SCAN_JAR="../target/qr_scan.jar"
test -f $QR_SCAN_JAR || sh ../make_jar.sh

# check if QRDecode.class exists and is newer than or equal to QRDecode.java
if [ ! -f QRDecode.class ] || [ QRDecode.java -nt QRDecode.class ]; then
    echo "Compiling QRDecode.java..."
    javac -cp $QR_SCAN_JAR QRDecode.java
    if [ $? -ne 0 ]; then
        echo "Compilation failed"
        exit 1
    fi
fi

java -cp "$QR_SCAN_JAR:." QRDecode.java "$1"
