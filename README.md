# meta-oqs

This layer is an OpenEmbedded/Yocto layer dedicated to Open Quantum Safe, providing experimental integration and testing of quantum-safe cryptographic algorithms for embedded Linux systems.


## OQS (Open Quantum Safe)

The Open Quantum Safe (OQS) project aims to develop and integrate quantum-resistant cryptographic algorithms. These algorithms are designed to be secure against the potential future threat of quantum computers. The OQS project provides a C library, liboqs, which includes implementations of various quantum-safe algorithms.

OQS implements the latest [NIST-approved Post-Quantum Cryptography (pqc) algorithms](https://csrc.nist.gov/projects/post-quantum-cryptography).

For more information, visit the [Open Quantum Safe website](https://openquantumsafe.org).

> **Note:** While OpenSSL 3.5.x has added native support for NIST-approved PQC algorithms `liboqs` goes further by also including algorithms currently under evaluation in future NIST standardization rounds. Additionally `liboqs` is not tied to OpenSSL since it provides a standalone C library with bindings for C++, Rust and Python making it usable across different applications. More importantly the `oqs-provider` detects if OpenSSL 3.5+ is in use and automatically disables its own ML-KEM/ML-DSA implementations and deferring to OpenSSL's native code which is considered higher quality and more stable.

> **Disclaimer:** The OQS project and `liboqs` are **experimental** and should not be used in production environments. The implementations are may contain security vulnerabilities and have not undergone the same level of auditing as established cryptographic libraries. See the [liboqs security policy](https://github.com/open-quantum-safe/liboqs/security) for details.


## Software Stack

```
OpenSSL-based applications:                Non-OpenSSL applications:
(NGINX / OpenVPN / OpenSSH / curl / …)     (C / C++ / Rust / Python / GO)

              |                                       |
              v                                       |
          OpenSSL 3.x                                 |
              |                                       |
              v                                       |
          oqs-provider                                |
              \                                       /
               \                                     /
                v                                   v
                             liboqs
                                |
                                v
                   PQC algorithms (ML-KEM, ML-DSA, …)

```

## Configuration

This layer can be integrated in your layer(s) or built standalone using [kas-tool](https://github.com/siemens/kas):

To Enable `OpenSSL` with oqs support using `oqs-provider` you should set: 

`DISTRO_FEATURES:append = " oqs"`

This will make `OpenSSL` aware of Hybrid/PQC algorithms and set the default TLS group to:

  `X25519MLKEM768:mlkem768:x25519:prime256v1:x448:secp521r1:secp384r1`

Also enables `OpenSSH` with Hybrid and pure PQC key exchange support on top of classical algorithms.

> **Recommendation:** Hybrid key exchange such as `X25519MLKEM768` is the recommended approach as PQC algorithms are still maturing. Hybrid combines a classical algorithm (X25519) with a post-quantum one (MLKEM-768) providing the best balance between quantum resistance if PQC hold and classical security as a fallback if it doesn't.

## Language Bindings

This layer provides OQS bindings for multiple languages:

| Language | Recipe | Description |
|----------|--------|-------------|
| *C* | [`liboqs`](recipes-crypto/liboqs) | Core PQC library, includes `oqs_test_*` and `oqs_speed_*` binaries for testing and benchmarking |
| *C++* | [`liboqs-cpp`](recipes-crypto/liboqs-cpp) | C++ wrapper around liboqs, ships example binaries: `oqs_cpp_rand`, `oqs_cpp_kem`, `oqs_cpp_sig` |
| *Rust* | [`liboqs-rust`](recipes-crypto/liboqs-rust) | Rust crate with FFI bindings to liboqs (built with clang installs `rlib` artifacts) |
| *Python* | [`python3-liboqs`](recipes-devtools/python) | Python 3 bindings via CFFI/ctypes, ships example scripts: `oqs_python_rand`, `oqs_python_kem`, `oqs_python_sig` |


## OQS Speed Tests

The [`liboqs`](recipes-crypto/liboqs) recipe builds and installs the `oqs_speed_kem` and `oqs_speed_sig` benchmarking binaries (from the `liboqs-tests` package). These can be used on target to measure the performance of all compiled KEM and signature algorithms on the actual hardware.

## OQS Demos

The [`oqs-demos`](recipes-support/oqs-demos) recipe provides ready-to-use demonstration configurations for PQC/Hybrid TLS and VPN scenarios:

- **Device certificates**:  [`device-certs`](recipes-support/oqs-demos/files/device-certs.sh) script with a systemd unit that generates device certificates (Classical/PQC) signed by the bundled CAs
- **Nginx**:  Pre-configured for three modes: classical, hybrid and pure PQC TLS (ports `443`/`444`/`445`)
- **OpenVPN**: Server configurations for classical, hybrid and pure PQC tunnels (ports `1194`/`1195`/`1196`)
- **TLS handshake benchmark**: `tls-handshake-bench` script to measure TLS handshake latency using curl over multiple rounds

> **Note:** Hybrid and pure PQC key exchange (e.g. on ports `444`/`445` for Nginx, `1195`/`1196` for OpenVPN) require both endpoints to have PQC support enabled. To connect from a host without `oqs-provider`, you can use the [oqs-docker](https://github.com/embetrix/oqs-docker) container as a PQC-enabled client.

For further demo commands (PQC sign/verify, CMS, OpenVPN client, etc.), see the [oqs-demos README](recipes-support/oqs-demos/files/README.md).

### Quick Test 

#### Hybrid TLS Handshake with Google

Google servers already support MLKEM hybrid key exchange. You can verify Hybrid TLS key exchange is working on your target:

```sh
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h google.com -p 443 -n10  
Key Exchange: X25519MLKEM768
Running 10 TLS handshakes for https://google.com:443 ...
Average TLS handshake: 79.81 ms (10 rounds)
```

#### Hybrid TLS with Chrome Browser

Chrome natively supports `X25519MLKEM768` hybrid key exchange. You can connect from your browser to the target's Nginx hybrid TLS server:

1. Open url: `https://<target-ip>:444`
2. Accept the self-signed certificate warning
3. Click the lock icon → **Connection is secure** → **Certificate** to verify the connection details
4. Open DevTools (`F12`) → **Security** tab to confirm the key exchange is using `X25519MLKEM768`

<p align ="left"><img src=images/chrome.png width=712 height=180 /></p>

#### TLS Benchmark

From the target, benchmark the three Nginx TLS modes using the built-in script:

*Classical*
```sh
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 443 -n100
Key Exchange: x25519
Running 100 TLS handshakes for https://localhost:443 ...
Average TLS handshake: 60.15 ms (100 rounds)
```
*Hybrid*
```sh
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 444 -n100
Key Exchange: X25519MLKEM768
Running 100 TLS handshakes for https://localhost:444 ...
Average TLS handshake: 49.03 ms (100 rounds)
```
*PQC*
```sh
root@raspberrypi5-c8-bf-64:~# tls-handshake-bench -h localhost -p 445 -n100
Key Exchange: mlkem768
Running 100 TLS handshakes for https://localhost:445 ...
Average TLS handshake: 60.87 ms (100 rounds)
```

> **Note:** MLKEM operations run in microseconds so hybrid KEX adds almost no measurable overhead (~30 µs on raspberrypi5) compared to classical TLS

#### SSH 

To connect using hybrid PQC key exchange over SSH:

```sh
ssh root@<target-ip> -o KexAlgorithms=mlkem768x25519-sha256 -v
```

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

```sh
sudo bmaptool copy \
     build/tmp/deploy/images/raspberrypi5/oqs-demo-image-raspberrypi5.rootfs-<VERSION>.wic.bz2 \
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

| Machine | BSP Layer |
|---------|-----------|
| `qemux86-64` | poky |
| `beaglebone-yocto` | poky |
| `raspberrypi5` | meta-raspberrypi |
| `raspberrypi4-64` | meta-raspberrypi |
| `stm32mp157f-dk2` | meta-stm32mp15x |
| `imx8mq-phanbell` | meta-freescale / meta-coral-ai |
| `wandboard` | meta-freescale-3rdparty |

## Architecture Optimizations

[`liboqs`](recipes-crypto/liboqs) is built with architecture-specific optimizations when available:

- *x86-64*: ADX, AES-NI, AVX, AVX2, BMI1, BMI2, PCLMULQDQ, POPCNT, SSE/SSE2/SSE3
- *aarch64*: NEON, AES, SHA2, SHA3
- *Others*: built without hardware-specific optimizations (unsupported architecture mode)

## Layer Dependencies

This layer depends on:

- `meta-oe`, `meta-python`, `meta-networking`, `meta-webserver` (from meta-openembedded)
- `meta-clang` (clang toolchain required by [`liboqs`](recipes-crypto/liboqs) and [`liboqs-rust`](recipes-crypto/liboqs-rust))
- `meta-lts-mixins` (newer Rust toolchain for Scarthgap)
