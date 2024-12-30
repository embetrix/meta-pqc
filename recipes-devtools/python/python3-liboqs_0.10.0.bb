SUMMARY = "Python 3 bindings for liboqs"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=643965392cf23865058f5b4a6b3fecbc"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-python.git;branch=main;protocol=https"
# SRCREV tagged 0.10.0
SRCREV = "02198f9c3366cfafdea38a7830b82b9bd78bcb32"

S = "${WORKDIR}/git"

DEPENDS = "liboqs python3-setuptools-native"
RDEPENDS:${PN} += "python3-cffi python3-ctypes"

inherit setuptools3

BBCLASSEXTEND = "native nativesdk"
