#!/bin/sh
# generate pqc device certificate
# 

# Get device IP
DEVICE_IP=$(networkctl status | grep Address | awk '{print $2}')
if ! echo "$DEVICE_IP" | grep -Eq '^([0-9]{1,3}\.){3}[0-9]{1,3}$'; then
    echo "Invalid device IP: $DEVICE_IP"
    exit 1
fi

HOSTNAME=$(hostname)
# Generate self signed PQC device certificate
if [ ! -f pqc-ca-cert.pem ] || [ ! -f pqc-ca-key.pem ]; then
    echo "Please generate CA keys first"
    exit 1
fi

if [ ! -f pqc-device-key.pem ] || [ ! -f pqc-device-cert.pem ]; then

    openssl req -new -newkey dilithium3 -keyout pqc-device-key.pem \
            -out pqc-device-csr.pem -nodes \
            -subj "/C=DE/ST=BW/O=Embetrix/CN=$HOSTNAME" \
            -addext "subjectAltName=DNS:$HOSTNAME, DNS:localhost,IP:$DEVICE_IP,IP:127.0.0.1" || exit 1

    openssl x509 -req -in pqc-device-csr.pem -CA pqc-ca-cert.pem -CAkey pqc-ca-key.pem \
            -CAcreateserial -days 360 \
            -out pqc-device-cert.pem \
            -copy_extensions copy  || exit 1
fi
