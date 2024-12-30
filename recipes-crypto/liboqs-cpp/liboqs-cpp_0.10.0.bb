DESCRIPTION = "C++ bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7bfbe613dc8f360526e4a328251e8088"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-cpp.git;branch=main;protocol=https"
# SRCREV tagged 0.10.0
SRCREV = "18f8eab50a9c2e7ac649d74763dc28960439c353"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
