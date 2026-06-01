DESCRIPTION = "C++ bindings for liboqs library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3a7c2d87a74d51c6273ba64bc7c47ee"

SRC_URI = "git://github.com/open-quantum-safe/liboqs-cpp.git;branch=main;protocol=https"
# SRCREV tagged 0.12.0
SRCREV = "7e293be10a2c5978afdbf6eaba1b0bf5056e530a"

inherit pkgconfig cmake

DEPENDS = "liboqs"

do_compile:append () {
    ${CMAKE_VERBOSE} cmake --build '${B}' --target examples -- ${EXTRA_OECMAKE_BUILD}
}

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/rand  ${D}${bindir}/oqs_cpp_rand
    install -m 0755 ${B}/sig   ${D}${bindir}/oqs_cpp_sig
    install -m 0755 ${B}/kem   ${D}${bindir}/oqs_cpp_kem
}

PACKAGES += "${PN}-examples"
RDEPENDS:${PN} += "liboqs"
RDEPENDS:${PN}-examples += "liboqs"
FILES:${PN}:remove  = " ${bindir}/*"
FILES:${PN}-examples = "${bindir}"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
