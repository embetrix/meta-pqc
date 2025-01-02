# meta-pqc

This layer is an OpenEmbedded/Yocto layer dedicated to Post-Quantum Cryptography, providing integration and testing of quantum-safe cryptographic algorithms for embedded Linux systems. It supports OQS (Open Quantum Safe) and Botan.

## Note

Both OQS and Botan implement the latest NIST-approved Post-Quantum Cryptography (PQC) algorithms:

### Public-Key Encryption and Key-Establishment Algorithms
1. **CRYSTALS-Kyber**: A lattice-based key encapsulation mechanism (KEM).

### Digital Signature Algorithms
1. **CRYSTALS-Dilithium**: A lattice-based digital signature algorithm.
2. **FALCON**: A lattice-based digital signature algorithm.
3. **SPHINCS+**: A hash-based digital signature algorithm.

## OQS (Open Quantum Safe)

The Open Quantum Safe (OQS) project aims to develop and integrate quantum-resistant cryptographic algorithms. These algorithms are designed to be secure against the potential future threat of quantum computers. The OQS project provides a C library, liboqs, which includes implementations of various quantum-safe algorithms.

For more information, visit the [Open Quantum Safe website](https://openquantumsafe.org).

## Botan

Botan is a cryptographic library written in C++ that provides a wide range of cryptographic algorithms and protocols. It supports both classical and quantum-safe cryptographic algorithms. Botan is designed to be portable and efficient, making it suitable for use in embedded systems. Botan is also recommended by the German Federal Office for Information Security (BSI).

For more information, visit the [Botan website](https://botan.randombit.net).

## Build

This layer can be integrated in your layers or built standalone using [kas-tool](https://github.com/siemens/kas):

To Enable PQC you should set:

`DISTRO_FEATURES += "pqc"`

```sh
KAS_MACHINE=<MACHINE> kas build kas-pqc.yml
```

or using kas docker container:

```sh
KAS_MACHINE=<MACHINE> kas-container build kas-pqc.yml
```

for example:

```sh
KAS_MACHINE=qemux86-64 kas build kas-pqc.yml'
```

## Run in Qemu

```sh
KAS_MACHINE=qemux86-64 kas shell kas-pqc.yml -c 'runqemu kvm serialstdio nographic qemuparams="-m 1024"'
```

## Note

You can check the available machines that can be built using the following command:

```sh
find layers/ -name *.conf | grep machine
```
