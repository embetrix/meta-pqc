# PQC Root CA/key

Generated using:

```
openssl req -x509 -new -newkey dilithium3 -keyout pqc-ca-key.pem -out pqc-ca-cert.pem -nodes -subj "/O=Embetrix Root CA PQC"  -days 3650
```
