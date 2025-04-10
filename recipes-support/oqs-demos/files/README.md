# PQC Root CA/key

Generated using:

```
openssl req -x509 -new -newkey dilithium3 -keyout pqc-ca-key.pem -out pqc-ca-cert.pem -nodes -subj "/O=Embetrix Root CA PQC"  -days 3650
```

# OpenSSL TLS server 
```
openssl s_server -cert pqc-device-cert.pem -key pqc-device-key.pem -CAfile pqc-ca-cert.pem -groups kyber768:frodo640shake -www -tls1_3 -accept 4443
```

# connect to OpenSSL TLS server using openssl
```
echo "q" | openssl s_client  -groups frodo640shake -showcerts -connect localhost:4443
```

# connect to OpenSSL TLS server using curl
```
curl --curves kyber768 https://localhost:4443
```

# connect to NGINX TLS server using curl
```
curl -v --curves kyber768  https://localhost:4433
```

```
curl -v --curves X25519MLKEM768  https://localhost:1433
```