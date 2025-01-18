SUMMARY = "Python 3 bindings for liboqs"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aab9fa3330e68324b66127a00f0e1bba"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-python.git;branch=main;protocol=https"
# SRCREV tagged 0.12.0
SRCREV = "7906e7879a099fa34217035957d977314f99757d"

S = "${WORKDIR}/git"

DEPENDS = "liboqs python3-setuptools-native"
RDEPENDS:${PN} += "python3-cffi python3-ctypes"

inherit setuptools3

BBCLASSEXTEND = "native nativesdk"
