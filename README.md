# meta-oqs

This layer is an OpenEmbedded/Yocto layer dedicated to Open Quantum Safe, providing experimental integration and testing of quantum-safe cryptographic algorithms for embedded Linux systems.


## OQS (Open Quantum Safe)

The Open Quantum Safe (OQS) project aims to develop and integrate quantum-resistant cryptographic algorithms. These algorithms are designed to be secure against the potential future threat of quantum computers. The OQS project provides a C library, liboqs, which includes implementations of various quantum-safe algorithms.

OQS implements the latest [NIST-approved Post-Quantum Cryptography (pqc) algorithms](https://csrc.nist.gov/projects/post-quantum-cryptography).

For more information, visit the [Open Quantum Safe website](https://openquantumsafe.org).

## Build

This layer can be integrated in your layer(s) or built standalone using [kas-tool](https://github.com/siemens/kas):

To Enable OpenSSL with oqs support using `oqs-provider` you should set: `DISTRO_FEATURES += "oqs"`

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

## Build & Run for Qemu

```sh
KAS_MACHINE=qemux86-64 kas build kas-oqs.yml

KAS_MACHINE=qemux86-64 kas shell kas-oqs.yml -c 'runqemu kvm serialstdio nographic qemuparams="-m 1024"'
```

## Note

You can check the available machines that can be built using the following command:

```sh
find layers/ -name *.conf | grep machine
```
