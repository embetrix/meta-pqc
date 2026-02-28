# Root CA generation

## PQC Root CA/key

Generated using:

```
openssl req -x509 -new -newkey mldsa65 -keyout pqc-ca-key.pem -out pqc-ca-cert.pem -nodes -subj "/O=Embetrix PQC Root CA" -addext "keyUsage=critical,keyCertSign,cRLSign" -days 3650
```
## RSA Root CA/key
```
openssl req -x509 -new -newkey rsa:4096 -keyout ca-key.pem  -out ca-cert.pem -nodes -subj "/O=Embetrix Root CA" -addext "keyUsage=critical,keyCertSign,cRLSign" -days 3650
```

# TLS connection testing

## Connect to NGINX Classical server using curl
```
curl -v  https://localhost:443
```

## connect to NGINX PQC Hybrid TLS server using curl

```
curl -v   https://localhost:444
```


## connect to NGINX PQC TLS server using curl

```
curl -v   https://localhost:445
```

# Signature

# ML-DSA Sign/Verify
```
echo "Some Text" >  message.txt
openssl pkeyutl -sign -inkey pqc-device-key.pem -in message.txt -out signature.bin
openssl pkey -in pqc-device-key.pem -pubout -out pqc-device-pubkey.pem
openssl pkeyutl -verify -inkey pqc-device-key.pem -in message.txt -sigfile signature.bin
openssl pkeyutl -verify -pubin -inkey pqc-device-pubkey.pem -in message.txt -sigfile signature.bin
```


# ML-DSA CMS Sign/Verify
```
openssl cms -sign -md sha256 -in message.txt -signer pqc-device-cert.pem -inkey pqc-device-key.pem -binary -out signature.bin 
openssl cms -verify -in signature.bin -binary -content  message.txt -CAfile pqc-ca-cert.pem
```

# OpenVPN

## connect to classical key exchange:
```
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

## connect to PQC Hybrid key exchange:
```
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

## connect to pure PQC key exchange:
```
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
