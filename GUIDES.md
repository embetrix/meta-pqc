# OQS Demo Commands

This document contains commands and examples for generating keys, testing TLS connections, and running demos with the **Open Quantum Safe (OQS)** integrations.

---

## Root CA Generation

Commands used to generate the bundled Root CA certificates and keys.

### PQC Root CA (ML-DSA-65)

```bash
openssl req -x509 -new -newkey mldsa65 \
    -keyout pqc-ca-key.pem -out pqc-ca-cert.pem \
    -nodes -subj "/O=Embetrix PQC Root CA" \
    -addext "keyUsage=critical,keyCertSign,cRLSign" -days 3650
```

### Classical Root CA (RSA-4096)

```bash
openssl req -x509 -new -newkey rsa:4096 \
    -keyout ca-key.pem -out ca-cert.pem \
    -nodes -subj "/O=Embetrix Root CA" \
    -addext "keyUsage=critical,keyCertSign,cRLSign" -days 3650
```

---

## Digital Signatures (ML-DSA)

### Sign & Verify Message

Create a message and sign it using the Post-Quantum `ML-DSA` algorithm.

```bash
# 1. Create a message
echo "Some Text" > message.txt

# 2. Sign the message
openssl pkeyutl -sign -inkey pqc-device-key.pem -in message.txt -out signature.bin

# 3. Extract public key for verification
openssl pkey -in pqc-device-key.pem -pubout -out pqc-device-pubkey.pem

# 4. Verify with private key (sanity check)
openssl pkeyutl -verify -inkey pqc-device-key.pem -in message.txt -sigfile signature.bin

# 5. Verify with public key
openssl pkeyutl -verify -pubin -inkey pqc-device-pubkey.pem -in message.txt -sigfile signature.bin
```

### CMS Sign & Verify

Using Cryptographic Message Syntax (CMS).

```bash
# Sign
openssl cms -sign -md sha256 \
    -in message.txt \
    -signer pqc-device-cert.pem \
    -inkey pqc-device-key.pem \
    -binary -out signature.bin 

# Verify
openssl cms -verify \
    -in signature.bin -binary \
    -content message.txt \
    -CAfile pqc-ca-cert.pem
```

---

## TLS Connection Testing (NGINX)

Test the different NGINX server configurations using `curl`.

> **Note:** The examples below use `localhost` for running on the device itself. To connect from a different machine, replace `localhost` with the target device's IP address or hostname.

| Type | Port | Description |
|------|------|-------------|
| **Classical** | `443` | Standard TLS  |
| **Hybrid** | `444` | Post-Quantum Hybrid (X25519 + ML-KEM) |
| **PQC** | `445` | Pure Post-Quantum (ML-KEM) |

### 1. Classical TLS (Port 443)

```bash
curl -v https://localhost:443
```

### 2. PQC Hybrid TLS (Port 444)

```bash
curl -v https://localhost:444
```

### 3. Pure PQC TLS (Port 445)

```bash
curl -v https://localhost:445
```

---

## OpenVPN

> **Note:** The examples below using `--remote localhost` are for loopback testing on the device. To connect a real client, replace `localhost` with the server's IP address or hostname.

Connect to the OpenVPN server using different key exchange methods.

### 1. Classical Key Exchange (Port 1194)

```bash
openvpn \
  --client \
  --dev tun \
  --proto udp \
  --remote localhost 1194 \
  --ca   /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key  /opt/oqs-demos/certs/rsa-device-client-key.pem \
  --verb 5
```

### 2. PQC Hybrid Key Exchange (Port 1195)

```bash
openvpn \
  --client \
  --dev tun \
  --proto udp \
  --remote localhost 1195 \
  --ca   /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key  /opt/oqs-demos/certs/rsa-device-client-key.pem \
  --verb 5
```

### 3. Pure PQC Key Exchange (Port 1196)

```bash
openvpn \
  --client \
  --dev tun \
  --proto udp \
  --remote localhost 1196 \
  --ca   /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key  /opt/oqs-demos/certs/rsa-device-client-key.pem \
  --remote-cert-tls server \
  --verb 5
```

---

## Mosquitto Broker (MQTT)

> **Note:** The examples below use `-h localhost` to connect locally. To connect from a remote publisher/subscriber, replace `localhost` with the broker's IP address or hostname.

Test MQTT messaging across different security levels.

| Type | Port | Description |
|------|------|-------------|
| **Classical** | `8883` | Standard TLS  |
| **Hybrid** | `8884` | Post-Quantum Hybrid (X25519 + ML-KEM) |
| **PQC** | `8885` | Pure Post-Quantum (ML-KEM) |

### 1. Classical MQTT (Port 8883)

**Subscribe:**
```bash
mosquitto_sub -h localhost -p 8883 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -d
```

**Publish:**
```bash
mosquitto_pub -h localhost -p 8883 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -m "hello Classical" -d
```

### 2. PQC Hybrid MQTT (Port 8884)

**Subscribe:**
```bash
mosquitto_sub -h localhost -p 8884 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -d
```

**Publish:**
```bash
mosquitto_pub -h localhost -p 8884 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -m "hello Hybrid" -d
```

### 3. Pure PQC MQTT (Port 8885)

**Subscribe:**
```bash
mosquitto_sub -h localhost -p 8885 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -d
```

**Publish:**
```bash
mosquitto_pub -h localhost -p 8885 \
  --cafile /opt/oqs-demos/certs/ca-cert.pem \
  --cert /opt/oqs-demos/certs/rsa-device-client-cert.pem \
  --key /opt/oqs-demos/certs/rsa-device-client-key.pem \
  -t test/topic -m "hello Pure PQC" -d
```

---

## TLS Benchmark

From the target, benchmark the three Nginx TLS modes using the built-in script:

> **Note:** The examples below use `-h localhost` to run the benchmark locally on the device. To benchmark a remote target, replace `localhost` with the target's IP address or hostname.

### 1. Classical

```bash
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 443 -n100
Key Exchange: x25519
Running 100 TLS handshakes for https://localhost:443 ...
Average TLS handshake: 60.15 ms (100 rounds)
```

### 2. Hybrid

```bash
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 444 -n100
Key Exchange: X25519MLKEM768
Running 100 TLS handshakes for https://localhost:444 ...
Average TLS handshake: 49.03 ms (100 rounds)
```

### 3. PQC

```bash
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 445 -n100
Key Exchange: mlkem768
Running 100 TLS handshakes for https://localhost:445 ...
Average TLS handshake: 60.87 ms (100 rounds)
```

> **Note:** MLKEM operations run in microseconds so hybrid KEX adds almost no measurable overhead (~30 µs on raspberrypi5) compared to classical TLS

---

## SSH

Connect using different Key Exchange Algorithms (KEX).

### 1. Classical

```bash
ssh root@<target-ip> -v
```

### 2. Hybrid

```bash
ssh root@<target-ip> -o KexAlgorithms=mlkem768x25519-sha256 -v
```

### 3. PQC

```bash
ssh root@<target-ip> -o KexAlgorithms=mlkem768-sha256 -v
```

