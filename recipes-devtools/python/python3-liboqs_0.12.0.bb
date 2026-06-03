SUMMARY = "Python 3 bindings for liboqs"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aab9fa3330e68324b66127a00f0e1bba"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-python.git;branch=main;protocol=https"
# SRCREV tagged 0.12.0
SRCREV = "7906e7879a099fa34217035957d977314f99757d"

DEPENDS = "liboqs python3-setuptools-native"
RDEPENDS:${PN} += "python3-cffi python3-ctypes"

inherit setuptools3

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/examples/rand.py  ${D}${bindir}/oqs_python_rand
    install -m 0755 ${B}/examples/sig.py   ${D}${bindir}/oqs_python_sig
    install -m 0755 ${B}/examples/kem.py   ${D}${bindir}/oqs_python_kem

    sed -i '1s|^|#!/usr/bin/env python3\n|' ${D}${bindir}/oqs_python_rand
    sed -i '1s|^|#!/usr/bin/env python3\n|' ${D}${bindir}/oqs_python_sig
    sed -i '1s|^|#!/usr/bin/env python3\n|' ${D}${bindir}/oqs_python_kem
}

# Put examples into separate packages
PACKAGE_BEFORE_PN += "${PN}-examples"
FILES:${PN}-examples = "${bindir}"
RDEPENDS:${PN}-examples = "${PN}"

BBCLASSEXTEND = "native nativesdk"
