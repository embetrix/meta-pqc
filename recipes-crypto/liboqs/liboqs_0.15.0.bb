DESCRIPTION = "Experimental Post-quantum cryptographic library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4b93ef2da47496727a4e8a59f443844e"

SRC_URI = "git://github.com/open-quantum-safe/liboqs.git;branch=main;protocol=https"
# SRCREV tagged 0.15.0
SRCREV = "97f6b86b1b6d109cfd43cf276ae39c2e776aed80"

S = "${WORKDIR}/git"

DEPENDS = "openssl"

inherit pkgconfig cmake

# Enable only NIST appoved Algorithms
#EXTRA_OECMAKE = "-DOQS_ALGS_ENABLED=STD"

# Cross-compiling so disable it
EXTRA_OECMAKE:append = " -DOQS_DIST_BUILD=OFF"

# Enable x86-64 optimizations
EXTRA_OECMAKE:x86-64:append = " \
    -DOQS_USE_ADX_INSTRUCTIONS=ON        \
    -DOQS_USE_AES_INSTRUCTIONS=ON        \
    -DOQS_USE_AVX_INSTRUCTIONS=ON        \
    -DOQS_USE_AVX2_INSTRUCTIONS=ON       \
    -DOQS_USE_BMI1_INSTRUCTIONS=ON       \
    -DOQS_USE_BMI2_INSTRUCTIONS=ON       \
    -DOQS_USE_PCLMULQDQ_INSTRUCTIONS=ON  \
    -DOQS_USE_POPCNT_INSTRUCTIONS=ON     \
    -DOQS_USE_SSE_INSTRUCTIONS=ON        \
    -DOQS_USE_SSE2_INSTRUCTIONS=ON       \
    -DOQS_USE_SSE3_INSTRUCTIONS=ON"

# Enable aarch64/arm64 optimizations
EXTRA_OECMAKE:aarch64:append = " \
    -DOQS_USE_ARM_NEON_INSTRUCTIONS=ON   \
    -DOQS_USE_ARM_AES_INSTRUCTIONS=ON    \
    -DOQS_USE_ARM_SHA2_INSTRUCTIONS=ON   \
    -DOQS_USE_ARM_SHA3_INSTRUCTIONS=ON"

# Enable build without optimizations for unsupported architectures
python () {
    arch = d.getVar('TARGET_ARCH')
    if arch not in ['x86_64', 'aarch64']:
        d.appendVar('EXTRA_OECMAKE', ' -DOQS_PERMIT_UNSUPPORTED_ARCHITECTURE=ON')
}

PACKAGECONFIG ?= "shared release"
PACKAGECONFIG[shared]  = "-DBUILD_SHARED_LIBS=ON, -DBUILD_SHARED_LIBS=OFF"
PACKAGECONFIG[openssl] = "-DOQS_USE_OPENSSL=ON, -DOQS_USE_OPENSSL=OFF"
PACKAGECONFIG[release] = "-DCMAKE_BUILD_TYPE=Release, -DOQS_USE_OPENSSL=Debug"
PACKAGECONFIG[minimal] = "-DQS_MINIMAL_BUILD=ON, -DQS_MINIMAL_BUILD=OFF"

do_install:append () {
    install -d ${D}${bindir}
    for f in ${B}/tests/test_*; do
      install -m 0755 $f  ${D}${bindir}/oqs_$(basename $f)
    done
    for f in ${B}/tests/speed_*; do
      install -m 0755 $f  ${D}${bindir}/oqs_$(basename $f)
    done
}

PACKAGES += "${PN}-tests"
FILES:${PN}:remove  = " ${bindir}/*"
RDEPENDS:${PN}-tests += "${PN}"
FILES:${PN}-tests = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
