# OQS Demo Commands

This document contains commands and examples for generating keys, testing TLS connections, and running demos with the **Open Quantum Safe (OQS)** integrations.

---

## 🔐 Root CA Generation

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

## 🌐 TLS Connection Testing (NGINX)

Test the different NGINX server configurations using `curl`.

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

## ✍️ Digital Signatures (ML-DSA)

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

## 🛡️ OpenVPN

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

## 📡 Mosquitto Broker (MQTT)

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

