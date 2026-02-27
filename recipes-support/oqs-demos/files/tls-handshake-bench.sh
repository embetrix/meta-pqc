#!/bin/sh -e
# SPDX-License-Identifier: MIT
# Copyright (C) 2026 Embetrix - https://www.embetrix.com
# Author: ayoub.zaki@embetrix.com
#
# TLS Handshake Benchmark
# Measures average TLS handshake time over multiple rounds using curl
#

HOST=""
PORT=443
ROUNDS=10
TIMEOUT=3
INSECURE=""

usage() {
    echo "Usage: $(basename "$0") -h HOST [-p PORT] [-n ROUNDS] [-t TIMEOUT] [-k]"
    echo ""
    echo "Options:"
    echo "  -h HOST     Target hostname or IP (required)"
    echo "  -p PORT     Target port (default: ${PORT})"
    echo "  -n ROUNDS   Number of handshake rounds (default: ${ROUNDS})"
    echo "  -t TIMEOUT  Connection timeout in seconds (default: ${TIMEOUT})"
    echo "  -k          Allow insecure TLS (skip certificate verification)"
    echo ""
    echo "Example: $(basename "$0") -h 192.168.1.10 -p 8443 -n 50"
    exit 1
}

while getopts "h:p:n:t:k" opt; do
    case "$opt" in
        h) HOST="$OPTARG" ;;
        p) PORT="$OPTARG" ;;
        n) ROUNDS="$OPTARG" ;;
        t) TIMEOUT="$OPTARG" ;;
        k) INSECURE="-k" ;;
        *) usage ;;
    esac
done

[ -z "$HOST" ] && { echo "Error: -h HOST is required."; usage; }

# Connectivity check + parse TLS key exchange in one shot
TLS_OUT=$(curl -o /dev/null -s -v --connect-timeout "$TIMEOUT" --max-time "$TIMEOUT" \
     $INSECURE "https://${HOST}:${PORT}" 2>&1) || {
    echo "Error: Cannot reach ${HOST}:${PORT} (timed out after ${TIMEOUT}s)."; exit 1
}
KEX=$(echo "$TLS_OUT" | grep -i "SSL connection using" | sed 's|.*/\([^/]*\)/\([^/]*\)$|\1|; s|^ *||; s| *$||' || true)
echo "Key Exchange: ${KEX:-(could not be detected)}"

echo "Running ${ROUNDS} TLS handshakes for https://${HOST}:${PORT} ..."

# Set CPU governor to performance for consistent benchmarking
if [ -d /sys/devices/system/cpu/cpu0/cpufreq ]; then
    for gov in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
        echo performance > "$gov" 2>/dev/null || true
    done
fi

for i in $(seq 1 "$ROUNDS"); do
    curl -o /dev/null -s --connect-timeout "$TIMEOUT" $INSECURE \
         -w "%{time_appconnect}\n" "https://${HOST}:${PORT}"
done | awk '{sum+=$1} END {printf "Average TLS handshake: %.2f ms (%d rounds)\n", (sum/NR)*1000, NR}'
