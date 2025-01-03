DESCRIPTION = "Experimental Post-quantum cryptographic library"
HOMEPAGE = "https://openquantumsafe.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4b93ef2da47496727a4e8a59f443844e"

SRC_URI = "git://github.com/open-quantum-safe/liboqs.git;branch=main;protocol=https"
# SRCREV tagged 0.12.0
SRCREV = "f4b96220e4bd208895172acc4fedb5a191d9f5b1"

S = "${WORKDIR}/git"

DEPENDS = "openssl"

inherit pkgconfig cmake

# https://github.com/open-quantum-safe/liboqs/blob/main/CONFIGURE.md#oqs_use_cpufeature_instructions
# FixMe: https://github.com/open-quantum-safe/liboqs/issues/2029
EXTRA_OECMAKE:x86-64 += "-DOQS_USE_CPUFEATURE_INSTRUCTIONS=ON \
                         -DOQS_USE_ADX_INSTRUCTIONS=ON -DOQS_USE_AES_INSTRUCTIONS=ON  \
                         -DOQS_USE_AVX_INSTRUCTIONS=ON -DOQS_USE_AVX2_INSTRUCTIONS=ON \
                         -DOQS_USE_AVX512_INSTRUCTIONS=ON -DOQS_USE_BMI1_INSTRUCTIONS=ON  \
                         -DOQS_USE_BMI2_INSTRUCTIONS=ON -DOQS_USE_PCLMULQDQ_INSTRUCTIONS=ON \
                         -DOQS_USE_VPCLMULQDQ_INSTRUCTIONS=ON -DOQS_USE_POPCNT_INSTRUCTIONS=ON \
                         -DOQS_USE_SSE_INSTRUCTIONS=ON -DOQS_USE_SSE2_INSTRUCTIONS=ON -DOQS_USE_SSE3_INSTRUCTIONS=ON"

EXTRA_OECMAKE:aarch64 += "-DOQS_USE_CPUFEATURE_INSTRUCTIONS=ON -DOQS_SPEED_USE_ARM_PMU=ON \
                          -DOQS_USE_ARM_AES_INSTRUCTIONS=ON -DOQS_USE_ARM_SHA2_INSTRUCTIONS=ON \
                          -DOQS_USE_ARM_SHA3_INSTRUCTIONS=ON -DOQS_USE_ARM_NEON_INSTRUCTION=ON"

EXTRA_OECMAKE += "-DOQS_PERMIT_UNSUPPORTED_ARCHITECTURE=ON"

PACKAGECONFIG ?= "shared release openssl"
PACKAGECONFIG[shared]  = "-DBUILD_SHARED_LIBS=ON, -DBUILD_SHARED_LIBS=OFF"
PACKAGECONFIG[openssl] = "-DOQS_USE_OPENSSL=ON, -DOQS_USE_OPENSSL=OFF"
PACKAGECONFIG[release] = "-DCMAKE_BUILD_TYPE=Release, -DOQS_USE_OPENSSL=Debug"
PACKAGECONFIG[minimal] = "-DQS_MINIMAL_BUILD=ON, -DQS_MINIMAL_BUILD=OFF"
PACKAGECONFIG[distributed] = "-DOQS_DIST_BUILD=ON, -DOQS_DIST_BUILD=OFF"

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/tests/test_*    ${D}${bindir}
    install -m 0755 ${B}/tests/speed_*   ${D}${bindir}
}

PACKAGES += "${PN}-tests"
FILES:${PN}:remove  = " ${bindir}/*"
RDEPENDS:${PN}-tests += "${PN}"
FILES:${PN}-tests = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
