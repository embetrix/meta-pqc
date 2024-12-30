DESCRIPTION = "C++ bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7bfbe613dc8f360526e4a328251e8088"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-cpp.git;branch=main;protocol=https"
# SRCREV tagged 0.10.0
SRCREV = "18f8eab50a9c2e7ac649d74763dc28960439c353"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

DEPENDS = "openssl liboqs"

do_compile:append () {
    ${CMAKE_VERBOSE} cmake --build '${B}' --target examples -- ${EXTRA_OECMAKE_BUILD}
}

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/rand  ${D}${bindir}/cpp-rand-example
    install -m 0755 ${B}/sig   ${D}${bindir}/cpp-sig-example
    install -m 0755 ${B}/kem   ${D}${bindir}/cpp-kem-example
}

PACKAGES += "${PN}-examples"
RDEPENDS:${PN} += "liboqs"
RDEPENDS:${PN}-examples += "liboqs"
FILES:${PN}:remove  = " ${bindir}/*"
FILES:${PN}-examples = "${bindir}"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
