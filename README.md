# meta-oqs

This layer is an OpenEmbedded/Yocto layer dedicated to Open Quantum Safe, providing experimental integration and testing of quantum-safe cryptographic algorithms for embedded Linux systems.


## OQS (Open Quantum Safe)

The Open Quantum Safe (OQS) project aims to develop and integrate quantum-resistant cryptographic algorithms. These algorithms are designed to be secure against the potential future threat of quantum computers. The OQS project provides a C library, liboqs, which includes implementations of various quantum-safe algorithms.

OQS implements the latest [NIST-approved Post-Quantum Cryptography pqc algorithms](https://csrc.nist.gov/projects/post-quantum-cryptography).

For more information, visit the [Open Quantum Safe website](https://openquantumsafe.org).

> **Note:** While OpenSSL 3.5.x has added native support for NIST-approved PQC algorithms `liboqs` goes further by also including algorithms currently under evaluation in future NIST standardization rounds. Additionally `liboqs` is not tied to OpenSSL since it provides a standalone C library with bindings for different programming languages making it usable across different applications. More importantly the `oqs-provider` detects if OpenSSL 3.5+ is in use and automatically disables its own ML-KEM/ML-DSA implementations and deferring to OpenSSL's native code which is considered higher quality and more stable.

**Disclaimer:** The OQS project and `liboqs` are **experimental** and should not be used in production environments. The implementations may contain security vulnerabilities and have not undergone the same level of auditing as established cryptographic libraries. Side-channel and timing attacks are not part of the OQS threat model. See the [liboqs security policy](https://github.com/open-quantum-safe/liboqs/security) for more details.


## Architecture Overview

```
    +-------------------------------+        +--------------------------------+
    |  OpenSSL-based Applications   |        |    Non-OpenSSL Applications    |
    | (NGINX, OpenSSH, curl, MQTT)  |        | (C, C++, Rust, Python, Go...)  |
    +---------------+---------------+        +---------------+----------------+
                    |                                        |
                    v                                        |
            +---------------+                                |
            |  OpenSSL 3.x  |                                |
            +-------+-------+                                |
                    |                                        |
                    v                                        |
            +---------------+                                |
            | oqs-provider  |                                |
            +-------+-------+                                |
                    |                                        |
                    +-----------------+    +-----------------+
                                      |    |
                                      v    v
                                +----------------+
                                |     liboqs     |
                                +-------+--------+
                                        |
                                        v
                                +----------------+
                                | PQC Algorithms |
                                +----------------+
```

## Configuration

This layer can be integrated in your layer(s) or built standalone using [kas-tool](https://github.com/siemens/kas):

To Enable `OpenSSL` with oqs support using `oqs-provider` you should set: 

`DISTRO_FEATURES:append = " oqs"`

This will make `OpenSSL` aware of Hybrid/PQC algorithms and set the default TLS group to:

  `X25519MLKEM768:mlkem768:x25519:prime256v1:x448:secp521r1:secp384r1`

Also enables `OpenSSH` (fork from [open-quantum-safe/openssh](https://github.com/open-quantum-safe/openssh)) with Hybrid and pure PQC key exchange support on top of classical algorithms.

**Recommendation:** Hybrid key exchange such as `X25519MLKEM768` is the recommended approach as PQC algorithms are still maturing. Hybrid combines a classical algorithm `X25519` with a post-quantum one `MLKEM-768` providing the best balance between quantum resistance if PQC hold and classical security as a fallback if it doesn't.

## Language Bindings

This layer provides OQS bindings for multiple languages:

| Language | Recipe | Description |
|----------|--------|-------------|
| *C* | [`liboqs`](recipes-crypto/liboqs) | Core PQC library, includes `oqs_test_*` and `oqs_speed_*` binaries for testing and benchmarking |
| *C++* | [`liboqs-cpp`](recipes-crypto/liboqs-cpp) | C++ bindings, ships example binaries: `oqs_cpp_rand`, `oqs_cpp_kem`, `oqs_cpp_sig` |
| *GO* | [`liboqs-go`](recipes-crypto/liboqs-go) | GO bindings, ships example binaries: `oqs_go_rand`, `oqs_go_kem`, `oqs_go_sig` |
| *Python* | [`python3-liboqs`](recipes-devtools/python) | Python 3 bindings, ships example scripts: `oqs_python_rand`, `oqs_python_kem`, `oqs_python_sig` |
| *Rust* | [`liboqs-rust`](recipes-crypto/liboqs-rust) | Rust crate bindings built with clang ships `rlib` artifacts |


## OQS Speed Tests
The [`liboqs`](recipes-crypto/liboqs) recipe builds and installs the `oqs_speed_kem` and `oqs_speed_sig` benchmarking binaries (from the `liboqs-tests` package). These can be used on target to measure the performance of all available KEM and signature algorithms on the target.

## OQS Demos

The [`oqs-demos`](recipes-support/oqs-demos) recipe provides ready-to-use demonstration configurations for PQC/Hybrid TLS and VPN scenarios:

- *`Device certificates`*:  [`device-certs`](recipes-support/oqs-demos/files/device-certs.sh) script with a systemd unit that generates device certificates (Classical/PQC) signed by the bundled CAs.
  > **Warning:** The bundled Root CA keys (`ca-key.pem`, `pqc-ca-key.pem`) are public and part of the demo. **not** to use them for production !
- *`Nginx`*:  Server configurations for classical, hybrid and pure PQC TLS on ports `443`|`444`|`445`
- *`OpenVPN`*: Server configurations for classical, hybrid and pure PQC tunnels on ports `1194`|`1195`|`1196` 
- *`Mosquitto`*: MQTT broker configurations for classical, hybrid and pure PQC TLS on ports `8883`|`8884`|`8885` 
- *`Hostname Setup`*: Automatically sets a unique hostname on boot based on MAC address ensuring distinct identities for fleets of devices.
- *`TLS handshake benchmark`*: [`tls-handshake-bench`](recipes-support/oqs-demos/files/tls-handshake-bench.sh) script to measure TLS handshake latency using curl over multiple rounds

> **Note:** Hybrid and pure PQC key exchange on ports `444`|`445` for Nginx, `1195`|`1196` for OpenVPN and `8884`|`8885` for MQTT require both endpoints to have PQC support enabled. To connect from a host without `oqs-provider`, you can use the [oqs-docker](https://github.com/embetrix/oqs-docker) container as a PQC-enabled client.

For further demo commands (PQC Signature, Curl, SSH, OpenVPN, MQTT) see the [GUIDES](GUIDES.md).

### Quick Test 

#### Hybrid TLS Handshake with Google

Google servers already support `X25519MLKEM768` hybrid key exchange. You can verify it's working from your target:

<pre>
root@raspberrypi5-c8-bf-64:~# curl -v https://google.com
* Host google.com:443 was resolved.
* IPv4: 142.250.201.78
*   Trying 142.250.201.78:443...
* Connected to google.com (142.250.201.78) port 443
* ALPN: curl offers http/1.1
* TLSv1.3 (OUT), TLS handshake, Client hello (1):
*  CAfile: /etc/ssl/certs/ca-certificates.crt
*  CApath: none
* TLSv1.3 (IN), TLS handshake, Server hello (2):
* TLSv1.3 (IN), TLS handshake, Encrypted Extensions (8):
* TLSv1.3 (IN), TLS handshake, Certificate (11):
* TLSv1.3 (IN), TLS handshake, CERT verify (15):
* TLSv1.3 (IN), TLS handshake, Finished (20):
* TLSv1.3 (OUT), TLS change cipher, Change cipher spec (1):
* TLSv1.3 (OUT), TLS handshake, Finished (20):
<mark>* SSL connection using TLSv1.3 / TLS_AES_256_GCM_SHA384 / X25519MLKEM768 / id-ecPublicKey</mark>
* ALPN: server accepted http/1.1
* Server certificate:
*  subject: CN=*.google.com
*  start date: Feb  2 08:36:42 2026 GMT
*  expire date: Apr 27 08:36:41 2026 GMT
*  subjectAltName: host "google.com" matched cert's "google.com"
*  issuer: C=US; O=Google Trust Services; CN=WE2
*  SSL certificate verify ok.
*   Certificate level 0: Public key type EC/prime256v1 (256/128 Bits/secBits), signed using ecdsa-with-SHA256
*   Certificate level 1: Public key type EC/prime256v1 (256/128 Bits/secBits), signed using ecdsa-with-SHA384
*   Certificate level 2: Public key type EC/secp384r1 (384/192 Bits/secBits), signed using ecdsa-with-SHA384
* using HTTP/1.x
> GET / HTTP/1.1
> Host: google.com
> User-Agent: curl/8.7.1
> Accept: */*
</pre>

#### Hybrid TLS with Chrome Browser

Chrome natively supports `X25519MLKEM768` hybrid key exchange. You can connect from your browser to the target's Nginx hybrid TLS server:

1. Open url: `https://<target-ip>:444`
2. Accept the self-signed certificate warning
3. Click the lock icon → **Connection is secure** → **Certificate** to verify the connection details
4. Open DevTools (`F12`) → **Security** tab to confirm the used key exchange:

<p align ="left"><img src=images/chrome.png width=712 height=180 /></p>

## Build

```sh
KAS_MACHINE=<MACHINE> kas build kas-oqs.yml
```

or using kas docker container:

```sh
KAS_MACHINE=<MACHINE> kas-container build kas-oqs.yml
```

for example:

```sh
KAS_MACHINE=raspberrypi5 kas build kas-oqs.yml
```

## Prepare SDCard

Flash image on a SD Card using [bmap-tool](https://github.com/yoctoproject/bmaptool):

```sh
sudo bmaptool copy \
     build/tmp/deploy/images/<MACHINE>/oqs-demo-image-<MACHINE>.rootfs-<VERSION>.wic.bz2 \
     /dev/mmcblk0
```

## Emulation with Qemu

Build for `qemux86-64`:
```sh
KAS_MACHINE=qemux86-64 kas build kas-oqs.yml
```

Run:
```sh
KAS_MACHINE=qemux86-64 kas shell kas-oqs.yml -c 'runqemu kvm serialstdio nographic qemuparams="-m 1024"'
```

## Tested Machines

| Machine | BSP Layer | CPU | Core | Arch | PQC Optimized |
|---------|-----------|-----|------|------|:----------------:|
| `qemux86-64` | poky | Generic x86-64 | core2 | x86_64 | Yes |
| `beaglebone-yocto` | poky | TI AM335x | Cortex-A8 | ARMv7 | No |
| `raspberrypi5` | meta-raspberrypi | BCM2712 | Cortex-A76 | AArch64 | Yes |
| `raspberrypi4-64` | meta-raspberrypi | BCM2711 | Cortex-A72 | AArch64 | Yes |
| `stm32mp157f-dk2` | meta-stm32mp15x | STM32MP157F | Cortex-A7 | ARMv7 | No |
| `imx8mq-phanbell` | meta-coral-ai | NXP i.MX 8M Quad | Cortex-A53 | AArch64 | Yes |
| `wandboard` | meta-freescale-3rdparty | NXP i.MX 6 | Cortex-A9 | ARMv7 | No |

## Layer Dependencies

This layer depends on:

- [`meta-openembedded`](https://github.com/openembedded/meta-openembedded/tree/scarthgap) (`meta-oe`, `meta-python`, `meta-networking`, `meta-webserver`)
- [`meta-clang`](https://github.com/kraj/meta-clang/tree/scarthgap) (clang toolchain)
- [`meta-lts-mixins`](https://git.yoctoproject.org/meta-lts-mixins/log/?h=scarthgap/rust) (newer Rust toolchain for scarthgap)
