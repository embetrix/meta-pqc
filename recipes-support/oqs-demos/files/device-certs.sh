#!/bin/sh
# SPDX-License-Identifier: MIT
# Copyright (C) 2026 Embetrix - https://www.embetrix.com
# Author: ayoub.zaki@embetrix.com
#
# Generate PQC and classical (RSA) device certificates
# Usage: device-certs.sh [--force]
#   --force: regenerate all device certificates even if they already exist


if [ "$1" = "--force" ]; then
    echo "Force mode: regenerating all device certificates"
    rm -f pqc-device-*.pem
    rm -f rsa-device-*.pem
fi

# Get device IPv4 address
DEVICE_IP=$(networkctl status | grep -oE '([0-9]{1,3}\.){3}[0-9]{1,3}' | grep -v '^127\.' | head -1)
if ! echo "$DEVICE_IP" | grep -Eq '^([0-9]{1,3}\.){3}[0-9]{1,3}$'; then
    echo "Invalid device IP: $DEVICE_IP"
    exit 1
fi

HOSTNAME=$(hostname)
# Generate self signed PQC device certificates (CLIENT and SERVER)
if [ ! -f pqc-ca-cert.pem ] || [ ! -f pqc-ca-key.pem ]; then
    echo "Please generate CA keys first"
    exit 1
fi

if [ ! -f pqc-device-server-key.pem ] || [ ! -f pqc-device-server-cert.pem ]; then

    openssl req -new -newkey mldsa65 -keyout pqc-device-server-key.pem \
            -out pqc-device-server-csr.pem -nodes \
            -subj "/C=DE/ST=BW/O=Embetrix/OU=PQC-DeviceCert/CN=$HOSTNAME" \
            -addext "subjectAltName=DNS:$HOSTNAME, DNS:localhost,IP:$DEVICE_IP,IP:127.0.0.1" \
            -addext "keyUsage=digitalSignature" \
            -addext "extendedKeyUsage=serverAuth" || exit 1

    openssl x509 -req -in pqc-device-server-csr.pem -CA pqc-ca-cert.pem -CAkey pqc-ca-key.pem \
            -CAcreateserial -days 3600 \
            -out pqc-device-server-cert.pem \
            -copy_extensions copy  || exit 1
fi

if [ ! -f pqc-device-client-key.pem ] || [ ! -f pqc-device-client-cert.pem ]; then

    openssl req -new -newkey mldsa65 -keyout pqc-device-client-key.pem \
            -out pqc-device-client-csr.pem -nodes \
            -subj "/C=DE/ST=BW/O=Embetrix/OU=PQC-ClientCert/CN=client-$HOSTNAME" \
            -addext "subjectAltName=DNS:client-$HOSTNAME, DNS:localhost,IP:$DEVICE_IP,IP:127.0.0.1" \
            -addext "keyUsage=digitalSignature" \
            -addext "extendedKeyUsage=clientAuth" || exit 1

    openssl x509 -req -in pqc-device-client-csr.pem -CA pqc-ca-cert.pem -CAkey pqc-ca-key.pem \
            -CAcreateserial -days 3600 \
            -out pqc-device-client-cert.pem \
            -copy_extensions copy  || exit 1
fi

# Generate self signed RSA device certificates (CLIENT and SERVER)
if [ ! -f ca-cert.pem ] || [ ! -f ca-key.pem ]; then
    echo "Please generate CA keys first"
    exit 1
fi

if [ ! -f rsa-device-server-key.pem ] || [ ! -f rsa-device-server-cert.pem ]; then

    openssl req -new -newkey rsa:4096 -keyout rsa-device-server-key.pem \
            -out rsa-device-server-csr.pem -nodes \
            -subj "/C=DE/ST=BW/O=Embetrix/OU=DeviceCert/CN=$HOSTNAME" \
            -addext "subjectAltName=DNS:$HOSTNAME, DNS:localhost,IP:$DEVICE_IP,IP:127.0.0.1" \
            -addext "keyUsage=digitalSignature" \
            -addext "extendedKeyUsage=serverAuth" || exit 1

    openssl x509 -req -in rsa-device-server-csr.pem -CA ca-cert.pem -CAkey ca-key.pem \
            -CAcreateserial -days 3600  \
            -out rsa-device-server-cert.pem \
            -copy_extensions copy  || exit 1
fi


if [ ! -f rsa-device-client-key.pem ] || [ ! -f rsa-device-client-cert.pem ]; then

    openssl req -new -newkey rsa:4096 -keyout rsa-device-client-key.pem \
            -out rsa-device-client-csr.pem -nodes \
            -subj "/C=DE/ST=BW/O=Embetrix/OU=DeviceCert/CN=client-$HOSTNAME" \
            -addext "subjectAltName=DNS:client-$HOSTNAME, DNS:localhost,IP:$DEVICE_IP,IP:127.0.0.1" \
            -addext "keyUsage=digitalSignature" \
            -addext "extendedKeyUsage=clientAuth" || exit 1

    openssl x509 -req -in rsa-device-client-csr.pem -CA ca-cert.pem -CAkey ca-key.pem \
            -CAcreateserial -days 3600 \
            -out rsa-device-client-cert.pem \
            -copy_extensions copy  || exit 1
fi
